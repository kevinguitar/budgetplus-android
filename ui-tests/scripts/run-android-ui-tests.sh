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

# Hard ceiling for a single flow. Maestro installs/uninstalls its instrumentation
# driver over adb around every flow; if the emulator dies mid-flow those adb calls
# block on the transport forever (there is no internal timeout), and the whole job
# then hangs until the 120-minute workflow limit cancels it. Bounding each flow with
# `timeout` turns that indefinite hang into a fast, recoverable failure so the
# offline-recovery/abort logic below can actually run. The longest healthy flow
# observed is ~2m, so 8m leaves generous headroom while still catching a wedge.
MAESTRO_FLOW_TIMEOUT="${MAESTRO_FLOW_TIMEOUT:-8m}"

# Resolve a `timeout` command (GNU coreutils on CI ships `timeout`; macOS/Homebrew
# ships `gtimeout`). If neither exists, fall back to running maestro directly.
TIMEOUT_BIN=$(command -v timeout || command -v gtimeout || true)

# When MAESTRO_OUTPUT_DIR is set (e.g. in CI), emit a per-suite JUnit XML report (so
# failures surface in the GitHub Actions run summary / PR checks via a test reporter) plus
# the debug output/screenshots for artifacts.
maestro_test() {
  local suite_name="$1"
  shift
  # Wrap maestro in `timeout` (when available) so a hung adb transport can never
  # stall a flow past MAESTRO_FLOW_TIMEOUT. `timeout` exits 124 on timeout, which
  # surfaces as a normal non-zero flow failure the recovery logic handles.
  local runner=("$MAESTRO_BIN")
  if [[ -n "$TIMEOUT_BIN" ]]; then
    runner=("$TIMEOUT_BIN" --signal=KILL "$MAESTRO_FLOW_TIMEOUT" "$MAESTRO_BIN")
  fi
  if [[ -n "${MAESTRO_OUTPUT_DIR:-}" ]]; then
    local out="$MAESTRO_OUTPUT_DIR/$suite_name"
    mkdir -p "$out"
    "${runner[@]}" test \
      --test-output-dir="$out" \
      --debug-output="$out" \
      --format=junit \
      --output="$out/report.xml" \
      "$@"
  else
    "${runner[@]}" test "$@"
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
    # bounce the adb server, which forces a reconnect and usually flips the device
    # back to `device`. Retry the bounce up to a few times (spaced out) rather than
    # once: a full emulator wedge sometimes needs more than a single reconnect
    # before the transport comes back.
    if [[ "$state" == "offline" && "$kicked_server" -lt 3 ]]; then
      echo "Device $ADB_SERIAL is offline; restarting adb server to force reconnect (attempt $((kicked_server + 1)))." >&2
      adb kill-server >/dev/null 2>&1 || true
      adb start-server >/dev/null 2>&1 || true
      kicked_server=$((kicked_server + 1))
      sleep 5
      continue
    fi

    # `wait-for-device` + `get-state == device` can lie: a zombie/wedged emulator
    # stays listed as `device` while adb transport commands hang. Actively probe it
    # with a trivial shell command under its own short `timeout` so a wedged device
    # is treated as not-ready instead of "healthy". `boot_completed` doubles as the
    # readiness signal, and the timeout guarantees this probe itself can't hang.
    if adb -s "$ADB_SERIAL" wait-for-device >/dev/null 2>&1 &&
      [[ "$(adb -s "$ADB_SERIAL" get-state 2>/dev/null | tr -d '[:space:]')" == "device" ]] &&
      [[ "$(probe_boot_completed)" == "1" ]]; then
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

# Run `getprop sys.boot_completed` under a short timeout (when available) so a
# wedged device that never answers the shell can't stall wait_for_device. Prints
# the trimmed value (empty on hang/failure).
probe_boot_completed() {
  local cmd=(adb -s "$ADB_SERIAL" shell getprop sys.boot_completed)
  if [[ -n "$TIMEOUT_BIN" ]]; then
    cmd=("$TIMEOUT_BIN" --signal=KILL 15 "${cmd[@]}")
  fi
  "${cmd[@]}" 2>/dev/null | tr -d '[:space:]'
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
  # Accept either a directory (run all its flows) or a single .yml file path.
  local flows
  if [[ -f "$dir" ]]; then
    flows=("$dir")
  else
    flows=("$dir"/*.yml)
  fi
  for flow in "${flows[@]}"; do
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

# --suites: invoked by emulators:exec (see below) to run just the flows. Propagate the
# real pass/fail result: `exit 0` here would swallow every flow failure and report the
# whole suite green, which is why CI failures were previously invisible.
if [[ "${1:-}" == "--suites" ]]; then
  run_suites
  exit $?
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
