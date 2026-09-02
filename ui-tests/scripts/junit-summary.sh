#!/usr/bin/env bash

# Writes a Markdown pass/fail table of the Maestro UI test results to the GitHub Actions
# job summary ($GITHUB_STEP_SUMMARY) so failures are visible directly on the run/PR without
# downloading artifacts. Reads every JUnit `report.xml` Maestro emitted under the given
# output directory.
#
# Usage: junit-summary.sh <platform-label> <maestro-output-dir>

set -uo pipefail

PLATFORM="${1:?platform label required}"
OUTPUT_DIR="${2:?output dir required}"
SUMMARY_FILE="${GITHUB_STEP_SUMMARY:-/dev/stdout}"

{
  echo "## ${PLATFORM} UI test results"
  echo ""
} >>"$SUMMARY_FILE"

# No reports at all usually means the build/emulator setup failed before any flow ran.
if ! find "$OUTPUT_DIR" -name 'report.xml' -type f 2>/dev/null | grep -q .; then
  {
    echo "> :warning: No JUnit reports were produced (the suite may have failed to start)."
    echo ""
  } >>"$SUMMARY_FILE"
  exit 0
fi

total_tests=0
total_failures=0
total_errors=0
total_skipped=0
rows=""

# Parse each <testsuite ...> element's aggregate attributes. Maestro emits one testsuite
# per invocation; robust enough to extract the counts with a small awk scan per file.
while IFS= read -r xml; do
  read -r t f e s name < <(awk '
    match($0, /<testsuite[^>]*>/) {
      block = substr($0, RSTART, RLENGTH)
      tests = failures = errors = skipped = 0; nm = ""
      if (match(block, /tests="[0-9]+"/))    { v = substr(block, RSTART, RLENGTH); gsub(/[^0-9]/, "", v); tests = v }
      if (match(block, /failures="[0-9]+"/)) { v = substr(block, RSTART, RLENGTH); gsub(/[^0-9]/, "", v); failures = v }
      if (match(block, /errors="[0-9]+"/))   { v = substr(block, RSTART, RLENGTH); gsub(/[^0-9]/, "", v); errors = v }
      if (match(block, /skipped="[0-9]+"/))  { v = substr(block, RSTART, RLENGTH); gsub(/[^0-9]/, "", v); skipped = v }
      if (match(block, /name="[^"]*"/))      { nm = substr(block, RSTART, RLENGTH); sub(/name="/, "", nm); sub(/"$/, "", nm) }
      print tests, failures, errors, skipped, nm
      exit
    }
  ' "$xml")

  [ -z "${t:-}" ] && continue
  # Derive a readable suite/flow name from the path relative to the output dir.
  rel="${xml#"$OUTPUT_DIR"/}"
  rel="${rel%/report.xml}"
  [ -n "$name" ] && rel="$name"

  total_tests=$((total_tests + t))
  total_failures=$((total_failures + f))
  total_errors=$((total_errors + e))
  total_skipped=$((total_skipped + s))

  bad=$((f + e))
  if [ "$bad" -gt 0 ]; then
    status=":x: FAIL"
  else
    status=":white_check_mark: pass"
  fi
  rows+="| ${rel} | ${status} | ${t} | ${f} | ${e} | ${s} |"$'\n'
done < <(find "$OUTPUT_DIR" -name 'report.xml' -type f | sort)

{
  echo "| Flow / Suite | Status | Tests | Failures | Errors | Skipped |"
  echo "|---|---|---|---|---|---|"
  printf '%s' "$rows"
  echo ""
  bad_total=$((total_failures + total_errors))
  if [ "$bad_total" -gt 0 ]; then
    echo "**Result: :x: ${bad_total} failing** (of ${total_tests} tests, ${total_skipped} skipped)."
  else
    echo "**Result: :white_check_mark: all ${total_tests} tests passing** (${total_skipped} skipped)."
  fi
  echo ""
} >>"$SUMMARY_FILE"
