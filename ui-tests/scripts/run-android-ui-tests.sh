#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
SERVICE_FILE="$ROOT_DIR/androidApp/google-services.json"
TEST_SERVICE_FILE="$ROOT_DIR/ui-tests/config/google-services.json"
RELEASE_SERVICE_FILE="$ROOT_DIR/misc/release/google-services.json"

restore_service_file() {
  local result=$?
  trap - EXIT
  set +e

  cp -p "$RELEASE_SERVICE_FILE" "$SERVICE_FILE"
  local restore_result=$?

  if ((restore_result != 0)); then
    printf 'Failed to restore %s from %s\n' "$SERVICE_FILE" "$RELEASE_SERVICE_FILE" >&2
    result=$restore_result
  fi

  exit "$result"
}

[[ -f "$RELEASE_SERVICE_FILE" ]]

trap restore_service_file EXIT
cp "$TEST_SERVICE_FILE" "$SERVICE_FILE"

cd "$ROOT_DIR"
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

run_suites() {
  # login runs first on freshly cleared state (its flows also clearState per-flow).
  adb shell pm clear com.kevlina.budgetplus
  for flow in ui-tests/login/*.yml; do
    # Skip iOS-only flows (per-flow platform gating).
    if grep -q '^platform: iOS' "$flow"; then
      continue
    fi
    maestro test "$flow"
  done

  # after-login/free re-provisions via setup-login on a fresh anonymous user.
  adb shell pm clear com.kevlina.budgetplus
  maestro test ui-tests/after-login/free

  # after-login/premium seeds premium via the uiTestPremium deeplink.
  adb shell pm clear com.kevlina.budgetplus
  maestro test ui-tests/after-login/premium
}

firebase --config ui-tests/config/firebase.json --project budgetplus-ui-tests \
  emulators:exec --only auth,firestore "$(declare -f run_suites); run_suites"
