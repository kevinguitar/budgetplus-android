# budgetplus-android
Budget+ Android client

## Internal User Ids

- **Alina**：BfvTnZPuUTS6oBH1UwZ8uZIllWs2
- **Kevin**：wStzA9aMwHd0pOGwyT0woBrf05q2
- **Kevin Test**：Z8anJ6T32nSCtWOJiB58DmgE8rl2

## Baseline Profile

How to run?
```
./gradlew :benchmark:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
```

Locate it in the build folder of the module you generated the profile in: [module]/build/outputs/connected_android_test_additional_output/benchmark/connected/[device].

Copy and rename the file to `baseline-prof.txt` and place it in the `src/main` directory of your app module

For Macrobenchmark:
```
./gradlew :benchmark:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=Macrobenchmark
```

## Firebase cloud messaging topics

We categorize the general push topic by language:
- general_en
- general_tw
- general_cn
- general_ja


## Supported deeplinks

- https://budgetplus.cchi.tw/record
- https://budgetplus.cchi.tw/overview
- https://budgetplus.cchi.tw/unlockPremium
- https://budgetplus.cchi.tw/settings?showMembers=true
- https://budgetplus.cchi.tw/colors?hex=cff1ff%3bdaf2cb%3b84c18f%3b596980

## References

1. Cloud Functions Repo: https://github.com/kevinguitar/budgetplus-cloud-functions
2. Firestore Charges: https://firebase.google.com/docs/firestore/pricing
