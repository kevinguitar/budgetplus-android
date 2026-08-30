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
  # login runs first on freshly cleared state (its flows also clearState per-flow).
  adb shell pm clear com.kevlina.budgetplus
  for flow in ui-tests/login/*.yml; do
    # Skip iOS-only flows (per-flow platform gating).
    if grep -q '^platform: iOS' "$flow"; then
      continue
    fi
    maestro_test "login/$(basename "$flow" .yml)" "$flow"
  done

  # after-login/free re-provisions via setup-login on a fresh anonymous user.
  adb shell pm clear com.kevlina.budgetplus
  maestro_test after-login-free ui-tests/after-login/free

  # after-login/premium seeds premium via the uiTestPremium deeplink.
  adb shell pm clear com.kevlina.budgetplus
  maestro_test after-login-premium ui-tests/after-login/premium
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
