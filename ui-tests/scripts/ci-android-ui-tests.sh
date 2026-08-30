#!/usr/bin/env bash

# CI entrypoint for android-emulator-runner. The runner invokes each *line* of an
# inline `script:` block as its own `/usr/bin/sh -c`, which silently breaks
# `set +e` and any post-run capture logic. Running a single script file avoids
# that: everything below shares one shell process.

set -x

ROOT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)
cd "$ROOT_DIR"

OUTPUT_DIR="build/maestro/android"
mkdir -p "$OUTPUT_DIR"

adb wait-for-device
until [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 2; done
adb shell input keyevent 82
adb shell settings put global hide_error_dialogs 1

# Do not abort on failure: we want to capture diagnostics before exiting.
set +e

# The runner script swaps in the test Firebase config, builds + installs the
# uiTest APK, sets up adb reverse, and runs the login/free/premium suites inside
# the Firebase emulators. It restores the production google-services.json on exit.
./ui-tests/scripts/run-android-ui-tests.sh
result=$?

adb exec-out screencap -p > "$OUTPUT_DIR/final-screen.png" || true
adb logcat -d > "$OUTPUT_DIR/logcat.txt" || true

exit "$result"
