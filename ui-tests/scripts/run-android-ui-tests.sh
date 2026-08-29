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
adb shell pm clear com.kevlina.budgetplus
firebase --config ui-tests/config/firebase.json --project budgetplus-ui-tests \
  emulators:exec --only auth,firestore "maestro test ui-tests"
