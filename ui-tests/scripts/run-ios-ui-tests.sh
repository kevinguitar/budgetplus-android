#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
SERVICE_FILE="$ROOT_DIR/iosApp/iosApp/GoogleService-Info.plist"
TEST_SERVICE_FILE="$ROOT_DIR/ui-tests/config/GoogleService-Info.plist"
RELEASE_SERVICE_FILE="$ROOT_DIR/misc/release/GoogleService-Info.plist"
SIMULATOR_NAME=${1:-iPhone 16 Pro}

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
xcodebuild build \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -sdk iphonesimulator \
  -destination "platform=iOS Simulator,name=$SIMULATOR_NAME" \
  -derivedDataPath build/ui-tests \
  CODE_SIGNING_ALLOWED=NO \
  ARCHS=arm64 \
  ONLY_ACTIVE_ARCH=YES \
  SWIFT_ACTIVE_COMPILATION_CONDITIONS='$(inherited) UI_TEST'
xcrun simctl uninstall booted com.kevlina.budgetplus || true
xcrun simctl install booted build/ui-tests/Build/Products/Debug-iphonesimulator/BudgetPlus.app
firebase --config ui-tests/config/firebase.json --project budgetplus-ui-tests \
  emulators:exec --only auth,firestore "maestro test ui-tests"
