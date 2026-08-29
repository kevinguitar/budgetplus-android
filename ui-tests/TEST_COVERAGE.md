# Budget+ — Maestro UI Test Coverage Plan

A full-regression UI test plan for the Budget+ KMP/Compose app, driven by
[Maestro](https://maestro.mobile.dev/), split into two independent suites:

1. **`login`** — runs unauthenticated (fresh app state); exercises auth + onboarding.
2. **`after-login`** — assumes the app is already authorized and has an accounting
   book, so launching lands directly on the **RecordScreen**.

Both suites run against the Firebase **auth + firestore emulators** using the
existing `uiTest` build type (Android) / `UI_TEST` compilation condition (iOS).
Both free and premium user experiences are covered.

---

## 1. Background — How the app decides what to show

Initial destination (`BookActivity` / iOS `NavigationInitActionProvider`):

| Condition | Destination |
|---|---|
| `authManager.userState == null` | `BookDest.Auth()` (login flow) |
| authed but `currentBookId == null` / no books | `BookDest.Welcome` (create/join book) |
| authed **and** has a book | **RecordScreen** (home) |

Test-mode facts (already implemented):
- `UiTestEnvironment.enabled` (set from Android `R.bool.is_ui_test` = true in the
  `uiTest` flavor; iOS `#if UI_TEST`) routes Firebase to the emulators and makes the
  auth buttons call `signInAnonymouslyForUiTest()` (anonymous emulator sign-in). It
  also disables the tutorial `Bubble` overlays.
- App id: `com.kevlina.budgetplus`. Emulator ports: **auth 9099**, **firestore 8080**.
- Premium is a server-side boolean `premium` field on the user's Firestore doc
  (`authManager.isPremium`). The RevenueCat paywall cannot complete a real purchase
  in CI, so premium UI is tested by **seeding `premium=true`**, not by purchasing.

### No test tags exist
There are currently **no `Modifier.testTag`/semantics IDs** in the codebase.
Selectors must use **visible text** (English locale) or the few icon
`contentDescription`s. A hardening task (§7) proposes adding stable test tags to the
highest-value controls; until then, tests run with the device locale forced to
English and rely on the string literals catalogued below.

---

## 2. Suite split & directory structure

Maestro runs an entire directory. We split into two folders, each run by its own
`maestro test` invocation so they are fully independent pipelines.

```
ui-tests/
  common/
    setup-login.yml          # subflow: anon sign-in + create "UI Test Book" -> Record
    seed-premium.yml         # subflow: flips the user to premium (see §6)
    assert-on-record.yml     # subflow: waits for/asserts "AC" visible
    add-record.yml           # subflow: params category, price -> creates one record
  login/                     # SUITE 1 — unauthenticated
    01-auth-screen.yml
    02-google-signin-to-welcome.yml
    03-create-book.yml
    04-create-book-validation.yml
    05-back-on-welcome-logs-out.yml
  after-login/               # SUITE 2 — pre-authorized, book exists
    free/
      ...                    # free-user cases (§4)
    premium/
      ...                    # premium cases, each begins with seed-premium (§5)
  config/                    # existing emulator/firebase config (unchanged)
  scripts/
    run-android-ui-tests.sh  # updated to run both suites (§8)
    run-ios-ui-tests.sh
```

### Achieving the "after login" precondition (chosen approach)
Each `after-login` flow starts by running the shared **`common/setup-login.yml`**
subflow (`runFlow`), which performs the exact steps of the current
`login-calculator.yml`: launch → `Continue with Google` (anonymous sign-in) →
enter book name `UI Test Book` → `Go` → assert `AC`. Because the emulator + app
state persist across flows within one `maestro test` run, the first flow provisions
auth+book and subsequent flows reuse it. `setup-login.yml` is written to be
idempotent: if `AC` is already visible (already provisioned), it returns
immediately; otherwise it runs the provisioning steps.

This keeps the two suites clean:
- `login/` flows **clear app state first** (`launchApp: clearState: true`) so they
  always start unauthenticated.
- `after-login/` flows call `setup-login` and never clear auth state (except the
  dedicated logout test, which runs last).

---

## 3. SUITE 1 — `login` (unauthenticated)

Runs on a freshly cleared app (`clearState: true`, emulator reset between runs by
the runner's `pm clear`).

| ID | Flow | Steps / Assertions |
|---|---|---|
| L1 | Auth screen renders | Launch (cleared). Assert `Welcome to Budget+`, `Continue with Google`. On iOS also assert `Continue with Apple`. |
| L2 | Google sign-in → Welcome | Tap `Continue with Google`. Assert Welcome content: `Book Name`, placeholder `My accounting book`, `Go`, and `You can create a new accounting book`. |
| L3 | Apple sign-in → Welcome (iOS only) | Tap `Continue with Apple` → same Welcome assertions. Gate with platform tag. |
| L4 | Create first book happy path | From Welcome, input `UI Test Book`, tap `Go`. Assert landing on Record (`AC` visible) and book name in top bar. |
| L5 | Create-book validation | On Welcome, assert `Go` is disabled/no-op with blank name; type a name, assert it becomes actionable. |
| L6 | Back on Welcome logs out | From Welcome, press back → assert returns to Auth screen (`Continue with Google`). (Run last in suite; re-provision not needed.) |

Notes:
- L4 is the canonical provisioning path reused by Suite 2.
- Snackbar assertions available: `Your book UI Test Book is created!`.

---

## 4. SUITE 2 — `after-login/free` (default anonymous user is free)

Every flow begins with `runFlow: ../../common/setup-login.yml`. The anonymous test
user has no `premium` flag → treated as free. Ads use test/fake ad units; assert on
navigation/UI, not on real ad content.

### 4.1 Record screen — calculator & core add-record
| ID | Flow | Key assertions |
|---|---|---|
| R1 | Digit entry & clear | Tap `7`,`8`,`9` → price shows `789`; tap `AC` → price back to `0`. |
| R2 | Decimal / 00 button | Enter `1`,`.`,`5` → `1.5`. (If DoubleZero mode set in settings, verify `00` inputs two zeros.) |
| R3 | Arithmetic evaluation | Enter `2`,`×`(icon),`3`, tap equals; result `6`. (Operators are icons — select by position; see §7 caveat.) |
| R4 | Add expense end-to-end | Select `Expense`, tap category `Food`, note `Lunch`, enter `120`, tap `OK`. Assert success (DoneAnimator) and screen reset (price `0`, no category selected). |
| R5 | Add income end-to-end | Select `Income`, note placeholder = `Where did you earn from?`, tap a category, enter price, `OK`. |
| R6 | Empty category guard | Enter price, no category, `OK` → snackbar `Please choose a category`. |
| R7 | Empty price guard | Select category, price `0`, `OK` → snackbar `Please input the price`. |
| R8 | Note defaults to category | Add record with blank note → record name equals category name (verify later in Overview). |

### 4.2 Book selector & book management
| ID | Flow | Key assertions |
|---|---|---|
| B1 | Open book dropdown | Tap top-bar book selector (desc `Select book`) → dropdown shows current book with check + `Create a New Book`. |
| B2 | Free books limit gate | Tap `Create a New Book` → because free limit = 1, item is locked; tapping routes to Unlock Premium (assert paywall entry per §5.4). |
| B3 | Invite | Tap `Invite` (desc) → OS share sheet appears (assert share intent / sheet). |

### 4.3 Categories
| ID | Flow | Key assertions |
|---|---|---|
| C1 | Enter Edit Categories | On Record, tap `Edit` (category area) → `Edit Categories` screen. |
| C2 | Add category | FAB `Add` → dialog title `Category`, type `Groceries`, `Add` → appears in list; `Save` (check icon) → snackbar `Category list has been saved`. |
| C3 | Rename category | Tap a cell → dialog → change name → `Rename` → save. |
| C4 | Delete category | Tap a cell → `Delete` → removed; save. |
| C5 | Duplicate name error | Add existing name → error `... already exist`. |
| C6 | Unsaved-changes guard | Make a change, press back → `Do you want to leave without saving the changes?` |
| C7 | Reorder | Long-press drag handle, reorder, save; verify new order reflected on Record grid. |

### 4.4 Overview / History
| ID | Flow | Key assertions |
|---|---|---|
| O1 | Navigate to Overview | Tap History bottom-nav (icon, by index). Assert `...'s Overview` title. |
| O2 | Type toggle | Toggle `Expense`/`Income`; list/chart updates. |
| O3 | Time period pills | Tap `1D`,`1W`,`1M`,`LM`; date range updates. |
| O4 | Free period limit | Attempt custom period > 1 month → snackbar `Unlock premium to set the period above one month` with `Go` → paywall. |
| O5 | Mode toggle | Toggle AllRecords ↔ GroupByCategories (menu icon); list ↔ chart. |
| O6 | Chart drill-down | GroupByCategories → tap a category → Records screen titled `<category>: <total>`. |
| O7 | Export CSV | Export icon (desc `Export csv`) → confirm dialog `Do you want to export...` → `Confirm`. Free user: interstitial ad flow runs first, then success `Your report has been saved to the Download folder` (grant storage permission on Android). |
| O8 | Balance card | With records present, assert `Total` and `Balance` labels + amounts. |

### 4.5 Records list, edit, delete, duplicate
| ID | Flow | Key assertions |
|---|---|---|
| E1 | Open record for edit | Drill to Records, tap a record → `Edit Record` dialog. |
| E2 | Edit & save | Change note/price/category/date → `Save` (enabled only when valid) → snackbar `Record edited`. |
| E3 | Delete record | Long-press → `Delete` → `Are you sure you want to delete the record?` → `Confirm` → snackbar `Record ... is deleted`. |
| E4 | Duplicate record | Long-press → `Duplicate` → snackbar `Record duplicated`. |
| E5 | Sort toggle | On Records, toggle sort (desc `Sort by price`/`Sort by date`); order changes. |

### 4.6 Search
| ID | Flow | Key assertions |
|---|---|---|
| S1 | Open search | Overview FAB (desc `Search`) → `Search` screen; field placeholder `Enter keyword`. |
| S2 | Keyword search | Type a keyword matching a seeded record → result appears. |
| S3 | Type / category / author filters | Exercise type pill (`Expense`/`Income`), category pill (`All Categories` → grid), author pill. |
| S4 | Period gating (free) | Open period sheet: `Last Month` allowed; `Last 6 Months`/`Last Year`/`Custom` show premium crown and route to paywall when tapped. |

### 4.7 Settings
| ID | Flow | Key assertions |
|---|---|---|
| ST1 | Open settings | Record top-bar `Settings` (desc) → `...'s Settings`. |
| ST2 | Rename user | `Rename User` → dialog `User Name` → `Rename` → success snackbar. |
| ST3 | View members | `View Members` → `Members` dialog; owner shows `Owner` label. |
| ST4 | Rename book | `Rename Book` → `Book Name` dialog → `Rename`. |
| ST5 | Edit book currency | `Edit Book Currency` → Currency Picker `Book's Currency`, search `US Dollar or USD`, pick → success. |
| ST6 | Preferred currency (free gate) | `Edit Preferred Currency` → for free user, converter usage on Record routes to paywall (verify §5.3). |
| ST7 | Allow members to edit | Owner-only switch toggles. |
| ST8 | Color tone picker (free) | `Color Tone Picker` → carousel; free tones `Oolong Milk Tea`/`Warm Tones of Dusk`/`Countryside` selectable + `Save`. Premium tones `Barbie and Ken`/`Azure Coast`/`Customized` show crown/locked → paywall. |
| ST9 | Input vibration switch | Toggle `Input Vibration`. |
| ST10 | Calculator decimal button | Dropdown `Dot`/`00`; verify effect on Record calculator. |
| ST11 | Chart mode | Dropdown `Bar Chart`/`Pie Chart`; verify Overview chart type. |
| ST12 | Hide Ads (free only) | `Hide Ads` item visible for free user → tap routes to paywall. |
| ST13 | Batch Record gate (free) | `Batch Record` → routes to paywall for free user. |
| ST14 | Logout | `Logout` → returns to Auth (`Continue with Google`). **Run last** in the free suite. |
| ST15 | Delete book / leave book | `Delete Book` → `Are you sure you want to delete ...?` → `Confirm` → book deleted (destructive; isolate near end). |
| ST16 | Delete account (two-step) | `Delete Account` → warning → `Delete Permanently?` → confirm. (Destructive; optional — see §9 ordering.) |

### 4.8 Ads (free)
| ID | Flow | Key assertions |
|---|---|---|
| A1 | Banner presence | On Record/Overview, assert banner ad area exists (Loading/Loaded/`...` not-available all acceptable). |
| A2 | Interstitial cadence | Add 7 records; assert interstitial appears on the 7th (or that flow proceeds if ad unavailable). Keep tolerant of ad load failures in CI. |

### 4.9 Notification permission
| ID | Flow | Key assertions |
|---|---|---|
| N1 | Permission prompt after 1st record | After first record, OS notification permission dialog appears; grant/deny handled (Maestro `tapOn` system dialog or `runFlow` permission handling). |

---

## 5. SUITE 2 — `after-login/premium`

Each premium flow runs `setup-login` then `seed-premium` (§6) so `isPremium=true`.
These assert the **unlocked** experience, complementing the free-user gate tests.

| ID | Flow | Key assertions |
|---|---|---|
| P1 | No banner ad | On Record/Overview, banner ad area is **absent**. |
| P2 | No interstitial on 7th record | Add 7 records; no interstitial; flow proceeds directly. |
| P3 | Create additional book | `Create a New Book` is **enabled** (no lock); create `Second Book`, `Copy categories from` `Default`, `Create` → snackbar `Your book Second Book is created!`; switch between books. |
| P4 | Overview period > 1 month | Set a custom period longer than a month → allowed, no upsell snackbar. |
| P5 | Search premium periods | `Last 6 Months`/`Last Year`/`Custom` selectable without paywall; results update. |
| P6 | Preferred currency + converter | `Edit Preferred Currency` → pick a currency differing from book → on Record the `CurrencySelector` wheel + converted amount show (no paywall); toggle book/preferred. |
| P7 | Premium color tones | Select `Barbie and Ken` / `Azure Coast` → `Save` succeeds (no paywall). `Customized` → color picker dialog (`Color Hex`), set hex, `Confirm`, `Save`; `Share` action visible. |
| P8 | Batch Record | Settings `Batch Record` → `Batch Record` screen; fill type/category/note/price, set `Start Date`, `Batch Frequency` (`Every` N `Month`/`Week`/`Day`), `Number of times`, tap `Record` → snackbar `... records added to ...`. Verify batched records show refresh icon and `Only this` / `All future records` on edit/delete. |
| P9 | Hide Ads item hidden | Settings does **not** show `Hide Ads`. |
| P10 | Premium crown in members | `View Members` shows PremiumCrown next to premium user. |

### 5.3 Preferred currency (referenced by ST6)
Free users tapping the currency converter on Record route to Unlock Premium; premium
users get the converter (covered by P6).

### 5.4 Paywall entry assertion
The Unlock Premium screen renders the RevenueCat paywall, whose content is
dashboard-driven and typically stays on a spinner in the emulator (no API key).
Therefore paywall **entry** is asserted by navigation (leaving the prior screen /
banner ad still hidden absence, spinner presence), **not** by paywall content.

---

## 6. Premium seeding mechanism (`common/seed-premium.yml`)

Premium is the Firestore `premium` flag on the (anonymous) user doc, which only
exists after sign-in. Two options, in order of preference:

1. **Test-only hook (recommended, small app change).** Add a
   `markPremiumForUiTest()` path mirroring the existing `signInAnonymouslyForUiTest`
   pattern, invokable only when `UiTestEnvironment.enabled` — e.g. triggered by a
   test-only deeplink `budgetplus://uiTestPremium` or a launch argument. The seed
   subflow then does `openLink budgetplus://uiTestPremium` and waits for snackbar
   `Budget+ Premium unlocked!`. This reuses `authManager.markPremium(true)` which
   already writes the flag and emits the snackbar.
2. **Direct emulator write (no app change).** A tiny Node/`curl` step writes
   `premium=true` to the user doc via the Firestore emulator REST API, keyed by the
   anonymous UID (retrieved from the auth emulator). Invoked by the runner script
   between provisioning and premium flows. More brittle (needs UID lookup) but keeps
   app code untouched.

Decision needed at implementation time; the plan assumes option 1 for reliability.
After seeding, the app observes the user doc and updates `isPremium` reactively — no
restart required, but flows should `extendedWaitUntil` for a premium-only affordance
(e.g. banner ad disappears) before asserting.

---

## 7. Selector strategy & test-tag hardening

**Now (text/desc based):** run the device in **English locale**; drive by the
literals catalogued in this plan. Stable icon `contentDescription`s available:
`Invite`, `Settings`, `Select book`, `Select Date`, `Search`, `Export csv`,
`Delete`, `Share`, `Save`, `Sort by price`/`Sort by date`.

**Caveats (elements not text/desc selectable — must use position/index):**
calculator operators (`+ - × ÷`, icons, `contentDescription=null`), the equals
icon, the mic (speak-to-record) button, bottom-nav tabs (icon-only), converted-price
icon.

**Recommended hardening task** — add `Modifier.testTag(...)` (exposed to Maestro as
`id:`) to these high-value controls so tests stop depending on localized text /
positional taps:
- Calculator: each digit, `AC`, `OK`/equals, operators, delete, mic.
- Record: price field, note field, `Expense`/`Income` tabs, category pills,
  book selector, converted-price row.
- Bottom nav: Add / History tabs.
- Overview: mode toggle, export, period pills, search FAB.
- Common dialogs: confirm/cancel/save/rename buttons.

This is optional but strongly recommended for long-term stability and iOS parity.

---

## 8. Runner & CI changes

### `scripts/run-android-ui-tests.sh` / `run-ios-ui-tests.sh`
Change the single `maestro test ui-tests` call to run the two suites explicitly so
they are separately reportable pipelines:

```bash
maestro test ui-tests/login
maestro test ui-tests/after-login
```

Keep the existing google-services swap + restore, `assembleUiTest`, install, and
`pm clear com.kevlina.budgetplus`. `pm clear` runs once before `login`; the
`after-login` suite relies on `setup-login` to re-provision within the same emulator
session. If option-2 premium seeding is chosen, insert the seed step before
`ui-tests/after-login/premium`.

### `.github/workflows/ui-tests.yml`
- Split the single Maestro invocation into `login` then `after-login` (both inside
  the same `firebase emulators:exec` so they share one emulator lifecycle), or run
  two `emulators:exec` blocks for full isolation.
- Preserve `--test-output-dir`, `--debug-output`, `--format=html` reporting; emit
  per-suite report subfolders (`build/maestro/android/login`,
  `.../after-login`) and upload both as artifacts.
- Both `android-ui-tests` and `ios-ui-tests` must pass (existing `ui-tests` gate).

---

## 9. Execution ordering & isolation rules

- **Login suite** always starts from a cleared, unauthenticated app.
- **After-login/free** ordering: non-destructive flows (calculator, categories,
  overview, search, most settings) first; then destructive ones last —
  `Delete book` (ST15), `Delete account` (ST16), `Logout` (ST14). Because these
  tear down the provisioned state, place them at the very end or in a dedicated
  final flow that re-provisions if needed.
- **After-login/premium** runs after free, each flow re-provisioning via
  `setup-login` + `seed-premium`. Prefer running premium flows in their own
  `emulators:exec` block (fresh emulator DB) to avoid cross-contamination from the
  free suite's mutations.
- Keep ad-dependent assertions **tolerant** (ads may fail to load in CI) — assert
  navigation/flow continues rather than requiring real ad content.
- Force **English locale** on the emulator/simulator (Android: `adb shell` locale
  set or app resource; iOS: simulator language) before running.

---

## 10. Coverage summary

| Area | Free | Premium |
|---|---|---|
| Auth (Google/Apple) | L1–L3 | n/a |
| Onboarding / create book | L4–L6 | P3 |
| Record calculator & add-record | R1–R8 | via P2/P6 |
| Book selector / switch / limits | B1–B3 | P3 |
| Categories CRUD & reorder | C1–C7 | shared |
| Overview / charts / export | O1–O8 | P4 |
| Records edit/delete/duplicate/sort | E1–E5 | P8 (batch) |
| Search + period gating | S1–S4 | P5 |
| Settings (all rows) | ST1–ST16 | P6/P7/P8/P9/P10 |
| Currency picker + converter | ST5/ST6 | P6 |
| Color tones | ST8 | P7 |
| Ads (banner/interstitial) | A1–A2 | P1–P2 |
| Notification permission | N1 | — |
| Premium paywall entry points | B2,O4,S4,ST8,ST12,ST13 | — |
| Premium unlocked experience | — | P1–P10 |

Out of scope: the insider **Push Notifications** admin tool (internal, Chinese-only,
not an end-user feature) and completing a real RevenueCat purchase (dashboard-driven
paywall cannot transact in the emulator; entry into the paywall is asserted instead).
