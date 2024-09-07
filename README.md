# Budget+ Android App（極簡記帳）
Budget+ is an easy to use co-spending tracker to track expenses together with your friends and family.

## Feature Overview

![overview_1](https://github.com/user-attachments/assets/b8a565ea-d7dd-4676-96a3-5ad02c37d8e3)

![overview_2](https://github.com/user-attachments/assets/a480d869-a814-41f1-a258-e24e61e38f2e)

### And some cool animated features! ✨
|                                                      Pie Chart                                                       |                                                  Color Tone Picker                                                   |                                                 Customize Color Tone                                                 |
|:--------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------:| 
| <video src="https://github.com/kevinguitar/budgetplus-android/assets/18852983/c2222bc4-f78e-42a2-a78b-ebdb78cc7c2e"> | <video src="https://github.com/kevinguitar/budgetplus-android/assets/18852983/82f6d3e9-8162-4554-809e-e2f81913e684"> | <video src="https://github.com/kevinguitar/budgetplus-android/assets/18852983/bece0f6d-2f49-4562-b24e-4ff51cf5d5a8"> |

## Download
https://play.google.com/store/apps/details?id=com.kevlina.budgetplus

## Tech stack
- [Jetpack Compose](https://developer.android.com/develop/ui/compose) for UI + MVVM Architecture
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) for asynchronous work
- [Hilt](https://dagger.dev/hilt/) for dependency injection
- [Lottie Compose](https://github.com/airbnb/lottie/blob/master/android-compose.md) for animation
- [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) for deeplink and navigation
- [Google Billing](https://developer.android.com/google/play/billing/integrate) for in-app purchases
- [Admob](https://admob.google.com/home/) with meta audience network mediation for Ads
- [Coil](https://github.com/coil-kt/coil) for image loading
- [Kotlinx serialization](https://github.com/Kotlin/kotlinx.serialization) for parsing JSON
- [Firebase Firestore](https://firebase.google.com/docs/firestore) for database
- Firebase [Authentication](https://firebase.google.com/docs/auth), [Analytics](https://firebase.google.com/docs/analytics), [Messaging](https://firebase.google.com/docs/cloud-messaging) and [Remote Config](https://firebase.google.com/docs/remote-config)
- [In-app Review](https://developer.android.com/guide/playcore/in-app-review) and [In-app Update](https://developer.android.com/guide/playcore/in-app-updates) integration
- [Baseline Profile](https://developer.android.com/topic/performance/baselineprofiles/overview) for speeding up app startup time
- [Mockk](https://github.com/mockk/mockk) and [Truth](https://github.com/google/truth) for unit testing

### Other used open source libraries
- [orchestra-colorpicker](https://github.com/skydoves/Orchestra?tab=readme-ov-file#colorpicker) for color picker view
- [reorderable](https://github.com/aclassen/ComposeReorderable) for reorder items in compose
- [kotlin-csv-jvm](https://github.com/jsoizo/kotlin-csv) for exporting data as csv file
- [detekt](https://github.com/detekt/detekt) for Kotlin static code analysis
- [exp4j](https://github.com/fasseg/exp4j?tab=readme-ov-file) for math expression evaluation

## Supported Deeplinks
- Home screen: https://budgetplus.cchi.tw/record
- Overview: https://budgetplus.cchi.tw/overview
- Unlock premium: https://budgetplus.cchi.tw/unlockPremium
- Settings: https://budgetplus.cchi.tw/settings
- Settings with members dialog: https://budgetplus.cchi.tw/settings?showMembers=true
- Sharing customized color tones: https://budgetplus.cchi.tw/colors?hex=cff1ff%3bdaf2cb%3b84c18f%3b596980

## Baseline Profile
The project uses [baseline profile](https://developer.android.com/topic/performance/baselineprofiles/overview) 
to speed up the app start up time.

How to run?
```bash
./gradlew :benchmark:connectedReleaseAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
```

Locate it in the build folder of the module you generated the profile in: [module]/build/outputs/connected_android_test_additional_output/release/connected/[device].

Copy and rename the file to `baseline-prof.txt` and place it in the `src/main` directory of your app module

For Macrobenchmark:
```bash
./gradlew :benchmark:connectedReleaseAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

## Firebase Cloud Functions
Database interactions and push notifications are all implemented with [Firebase Cloud Functions](https://firebase.google.com/docs/functions), 
the repo is opened source as well: https://github.com/kevinguitar/budgetplus-cloud-functions

## License
![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)
```
Copyright (c) 2024 kevinguitar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
