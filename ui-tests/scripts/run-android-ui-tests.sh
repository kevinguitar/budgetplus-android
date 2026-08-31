#!/usr/bin/env bash

set -euo pipefail

# This script has two modes:
#   * default:  swap in the test Firebase config, build + install the uiTest APK,
#               then run the Maestro suites inside the Firebase emulators.
#   * --suites: run only the Maestro suites (login/free/premium). This mode is
#               re-invoked by `firebase emulators:exec` below. It exists because
#               emulators:exec runs its command via /bin/sh (dash on CI), which
#               cannot see bash functions exported with `export -f`; re-running
#               this file with bash keeps all bash-specific logic working.

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$ROOT_DIR"

# Prefer the maestro on PATH; fall back to the default install location (CI installs it there).
MAESTRO_BIN=$(command -v maestro || echo "$HOME/.maestro/bin/maestro")

# When MAESTRO_OUTPUT_DIR is set (e.g. in CI), emit per-suite HTML reports + debug output.
maestro_test() {
  local suite_name="$1"
  shift
  if [[ -n "${MAESTRO_OUTPUT_DIR:-}" ]]; then
    local out="$MAESTRO_OUTPUT_DIR/$suite_name"
    mkdir -p "$out"
    "$MAESTRO_BIN" test \
      --test-output-dir="$out" \
      --debug-output="$out" \
      --format=html \
      --output="$out/report.html" \
      "$@"
  else
    "$MAESTRO_BIN" test "$@"
  fi
}

run_suites() {
  suites_failed=0

  # login runs first on freshly cleared state (its flows also clearState per-flow).
  wait_for_device || true
  adb -s "$ADB_SERIAL" shell pm clear com.kevlina.budgetplus
  run_suite_dir login ui-tests/login

  # after-login/free re-provisions via setup-login on a fresh anonymous user.
  wait_for_device || true
  adb -s "$ADB_SERIAL" shell pm clear com.kevlina.budgetplus
  run_suite_dir after-login-free ui-tests/after-login/free

  # after-login/premium seeds premium via the uiTestPremium deeplink.
  wait_for_device || true
  adb -s "$ADB_SERIAL" shell pm clear com.kevlina.budgetplus
  run_suite_dir after-login-premium ui-tests/after-login/premium

  return "$suites_failed"
}

# The emulator serial CI uses. android-emulator-runner always boots emulator-5554.
ADB_SERIAL="${ANDROID_SERIAL:-emulator-5554}"

# Block until the emulator is booted and responsive, or fail after a timeout.
#
# Each `maestro test` invocation installs/uninstalls its instrumentation driver.
# Over a long sequential suite that churn can briefly push the device `offline`,
# which surfaces downstream as `device 'emulator-5554' not found`. Waiting for the
# device to settle *between* flows turns a fatal cascade into a short pause: if the
# device recovers we continue; if it is genuinely gone we report and bail early
# instead of hammering a dead emulator for the remaining flows.
wait_for_device() {
  local deadline=$((SECONDS + 180))
  local kicked_server=0
  while ((SECONDS < deadline)); do
    local state
    state="$(adb -s "$ADB_SERIAL" get-state 2>/dev/null | tr -d '[:space:]')"

    # An `offline` device is listed but unresponsive: `adb wait-for-device` returns
    # immediately for it, so it never truly "settles". The reliable recovery is to
    # bounce the adb server once, which forces a reconnect and usually flips the
    # device back to `device`. Only do this once per wait to avoid thrashing.
    if [[ "$state" == "offline" && "$kicked_server" -eq 0 ]]; then
      echo "Device $ADB_SERIAL is offline; restarting adb server to force reconnect." >&2
      adb kill-server >/dev/null 2>&1 || true
      adb start-server >/dev/null 2>&1 || true
      kicked_server=1
      sleep 3
      continue
    fi

    if adb -s "$ADB_SERIAL" wait-for-device >/dev/null 2>&1 &&
      [[ "$(adb -s "$ADB_SERIAL" get-state 2>/dev/null | tr -d '[:space:]')" == "device" ]] &&
      [[ "$(adb -s "$ADB_SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '[:space:]')" == "1" ]]; then
      # Re-establish the Firebase emulator port forwards; a device that dropped and
      # came back loses its reverse tunnels.
      adb -s "$ADB_SERIAL" reverse tcp:9099 tcp:9099 >/dev/null 2>&1 || true
      adb -s "$ADB_SERIAL" reverse tcp:8080 tcp:8080 >/dev/null 2>&1 || true
      return 0
    fi
    sleep 3
  done
  return 1
}

# Run every flow in a suite directory ONE AT A TIME.
#
# Passing a directory to `maestro test` makes recent Maestro versions run all
# flows in that directory concurrently (each spins up its own Maestro session +
# instrumentation against the emulator). On the CI emulator that concurrency
# exhausts the device and it goes `offline` mid-run, cascading into
# `device 'emulator-5554' not found` for every remaining flow. Iterating files
# and invoking maestro per-flow keeps execution strictly sequential. Each flow is
# self-contained (it runs setup-login itself), so per-file execution is
# equivalent to the previous directory run, minus the crashing parallelism.
#
# A single failing flow must not abort the rest of the suite (the previous
# directory run reported every flow), so failures are captured and surfaced via
# `suites_failed` instead of tripping `set -e`.
#
# Before each flow we confirm the device is alive. When a flow fails we re-check:
# a flow that failed *because the device dropped* (offline / `device not found`)
# is retried once after the emulator recovers, so a transient blip does not count
# as a real failure. A flow that failed on a genuine assertion is left as a
# failure — no retry, since re-running would not change the outcome.
run_suite_dir() {
  local suite_prefix="$1"
  local dir="$2"
  for flow in "$dir"/*.yml; do
    # Skip iOS-only flows (per-flow platform gating).
    if grep -q '^platform: iOS' "$flow"; then
      continue
    fi

    local name
    name="$suite_prefix/$(basename "$flow" .yml)"

    if ! wait_for_device; then
      echo "::error::Emulator $ADB_SERIAL is unreachable before $name; aborting suite." >&2
      suites_failed=1
      return 1
    fi

    if maestro_test "$name" "$flow"; then
      continue
    fi

    # The flow failed. If the device is still healthy this is a real failure.
    if adb -s "$ADB_SERIAL" get-state 2>/dev/null | grep -q '^device$'; then
      suites_failed=1
      continue
    fi

    # Device dropped mid-flow: wait for it to come back and retry the flow once.
    echo "::warning::$name failed with the device offline; recovering emulator and retrying." >&2
    if ! wait_for_device; then
      echo "::error::Emulator $ADB_SERIAL did not recover after $name; aborting suite." >&2
      suites_failed=1
      return 1
    fi
    if ! maestro_test "$name" "$flow"; then
      suites_failed=1
    fi
  done
}

# --suites: invoked by emulators:exec (see below) to run just the flows.
if [[ "${1:-}" == "--suites" ]]; then
  run_suites
  exit 0
fi

SERVICE_FILE="$ROOT_DIR/androidApp/google-services.json"
TEST_SERVICE_FILE="$ROOT_DIR/ui-tests/config/google-services.json"
RELEASE_SERVICE_FILE="$ROOT_DIR/misc/release/google-services.json"

restore_service_file() {
  local result=$?
  trap - EXIT
  set +e

  # Only restore the production config when we actually have one to restore
  # (locally). In CI the production file is absent (gitignored), so there is
  # nothing to put back and we leave the test config in place.
  if [[ -f "$RELEASE_SERVICE_FILE" ]]; then
    cp -p "$RELEASE_SERVICE_FILE" "$SERVICE_FILE"
    local restore_result=$?
    if ((restore_result != 0)); then
      printf 'Failed to restore %s from %s\n' "$SERVICE_FILE" "$RELEASE_SERVICE_FILE" >&2
      result=$restore_result
    fi
  fi

  exit "$result"
}

# The test Firebase config must exist; the production one is optional (present
# locally, absent in CI where google-services.json files are gitignored).
[[ -f "$TEST_SERVICE_FILE" ]]

trap restore_service_file EXIT
cp "$TEST_SERVICE_FILE" "$SERVICE_FILE"

./gradlew :androidApp:assembleUiTest
adb install -r androidApp/build/outputs/apk/uiTest/androidApp-uiTest.apk

# The app routes Firebase to the emulators via 127.0.0.1; forward those ports from the
# device/emulator to the host so the auth + firestore emulators are reachable.
adb reverse tcp:9099 tcp:9099
adb reverse tcp:8080 tcp:8080

# Force English locale and disable the one-time Gboard stylus-handwriting tutorial that
# otherwise pops over text fields during the first onboarding input.
adb shell settings put secure stylus_handwriting_enabled 0 || true
adb shell settings put secure stylus_handwriting_default_value 0 || true

# Run the suites inside the emulator environment. emulators:exec runs its command
# via /bin/sh, so re-invoke this script with bash (see --suites note above).
export MAESTRO_BIN MAESTRO_OUTPUT_DIR

firebase --config ui-tests/config/firebase.json --project budgetplus-ui-tests \
  emulators:exec --only auth,firestore "bash ui-tests/scripts/run-android-ui-tests.sh --suites"
