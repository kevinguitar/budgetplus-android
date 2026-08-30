#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
SERVICE_FILE="$ROOT_DIR/iosApp/iosApp/GoogleService-Info.plist"
TEST_SERVICE_FILE="$ROOT_DIR/ui-tests/config/GoogleService-Info.plist"
RELEASE_SERVICE_FILE="$ROOT_DIR/misc/release/GoogleService-Info.plist"
SIMULATOR_NAME=${1:-iPhone 17}
SIMULATOR_ID=$(xcrun simctl list devices available | awk -v name="$SIMULATOR_NAME" 'index($0, name " (") && /\(Booted\)/ { id = $(NF - 1); gsub(/[()]/, "", id); print id; exit }')

if [[ -z "$SIMULATOR_ID" ]]; then
  printf 'No booted simulator named %s found\n' "$SIMULATOR_NAME" >&2
  exit 1
fi

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
  -destination "platform=iOS Simulator,id=$SIMULATOR_ID" \
  -derivedDataPath build/ui-tests \
  CODE_SIGN_IDENTITY=- \
  ARCHS=arm64 \
  ONLY_ACTIVE_ARCH=YES \
  SWIFT_ACTIVE_COMPILATION_CONDITIONS='$(inherited) UI_TEST'

APP_PATH="build/ui-tests/Build/Products/Debug-iphonesimulator/BudgetPlus.app"

# Prefer the maestro on PATH; fall back to the default install location (CI installs it there).
MAESTRO_BIN=$(command -v maestro || echo "$HOME/.maestro/bin/maestro")

# When MAESTRO_OUTPUT_DIR is set (e.g. in CI), emit per-suite HTML reports + debug output.
maestro_test() {
  local suite_name="$1"
  shift
  if [[ -n "${MAESTRO_OUTPUT_DIR:-}" ]]; then
    local out="$MAESTRO_OUTPUT_DIR/$suite_name"
    mkdir -p "$out"
    "$MAESTRO_BIN" test --udid "$SIMULATOR_ID" \
      --test-output-dir="$out" \
      --debug-output="$out" \
      --format=html \
      --output="$out/report.html" \
      "$@"
  else
    "$MAESTRO_BIN" test --udid "$SIMULATOR_ID" "$@"
  fi
}

# Force the simulator UI (and the app) to English so tests can match English strings.
xcrun simctl spawn "$SIMULATOR_ID" defaults write -g AppleLanguages '("en-US")'
xcrun simctl spawn "$SIMULATOR_ID" defaults write -g AppleLocale "en_US"

# Reinstalls the app from a fully reset state (fresh keychain wipes the persisted
# Firebase auth session, which launchApp:clearState does not clear on iOS).
reset_app() {
  xcrun simctl terminate "$SIMULATOR_ID" com.kevlina.budgetplus >/dev/null 2>&1 || true
  xcrun simctl uninstall "$SIMULATOR_ID" com.kevlina.budgetplus >/dev/null 2>&1 || true
  xcrun simctl keychain "$SIMULATOR_ID" reset >/dev/null 2>&1 || true
  xcrun simctl install "$SIMULATOR_ID" "$APP_PATH"
}

run_suites() {
  # login: each flow needs a truly unauthenticated start, so reset the keychain before
  # every flow (iOS persists Firebase auth in the keychain across clearState).
#  for flow in ui-tests/login/*.yml; do
#    # Honor per-flow platform gating (running a single file bypasses Maestro's own gate).
#    if grep -q '^platform: Android' "$flow"; then
#      continue
#    fi
#    reset_app
#    maestro_test "login/$(basename "$flow" .yml)" "$flow"
#  done
#
#  reset_app
#  maestro_test after-login-free ui-tests/after-login/free
#
#  reset_app
#  maestro_test after-login-premium ui-tests/after-login/premium

  reset_app
  maestro_test after-login-premium ui-tests/after-login/premium/05-search-premium-periods.yml
  maestro_test after-login-premium ui-tests/after-login/premium/07-premium-color-tones.yml
}

export SIMULATOR_ID APP_PATH MAESTRO_BIN
export -f maestro_test reset_app run_suites

firebase --config ui-tests/config/firebase.json --project budgetplus-ui-tests \
  emulators:exec --only auth,firestore run_suites
