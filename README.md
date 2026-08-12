# CLiTICAL for Android

*[日本語版 README はこちら](README.ja.md)*

CLiTICAL is a risk calculator for patients with **chronic limb-threatening ischaemia (CLTI)** who are
scheduled for revascularisation. From routine bedside data — demographics, nutrition, comorbidities,
lesion distribution and Rutherford class — it predicts peri-procedural and mid-term outcomes using
prediction models derived from the JCLIMB registry of the Japanese Society for Vascular Surgery
(JSVS).

This repository holds the Android app. An iOS counterpart (`clitical-ios`) exists, and the Android UI
deliberately mirrors its structure so both platforms present the same information in the same order.

> [!IMPORTANT]
> The predicted values are reference information intended to support clinical judgement. They do not
> replace diagnosis or treatment decisions, which remain the responsibility of the attending
> clinician.

## What it predicts

| Output | Meaning |
| --- | --- |
| **GNRI** | Geriatric Nutritional Risk Index, with a four-level nutritional risk classification (No / Low / Moderate / Major) |
| **30-day amputation or death** | Risk of major amputation and/or death within 30 days of revascularisation |
| **30-day MALE** | Risk of a major adverse limb event within 30 days (above-ankle amputation of the index limb, or major reintervention) |
| **2-year OS** | Predicted 2-year overall survival after revascularisation, classified as Low / Medium / High risk |
| **2-year AFS** | Predicted 2-year amputation-free survival after revascularisation |

GNRI is computed first and then feeds the survival models as a covariate, so nutritional status
influences every downstream prediction.

## Screens

The app has three destinations in a bottom navigation bar:

- **Risk Assessment** — the patient data form (basic info, social history, clinical info, artery
  lesion sites, other vascular lesions, complications), and the results screen it pushes to.
- **References** — the two source papers, opened in a Custom Tab.
- **Settings** — language switcher, terms of service, and app info.

Input is validated before calculation: the numeric fields must all be filled, and at least one
arterial lesion site must be selected. Otherwise the form reports the specific problem instead of
producing a number.

## References

1. Miyata T. et al. *Risk prediction model for early outcomes of revascularization for chronic
   limb-threatening ischaemia.* Br J Surg. 2022 Oct 14;109(11):1123.
   <https://doi.org/10.1093/bjs/znab036>
2. Miyata T. et al. *Prediction Models for Two Year Overall Survival and Amputation Free Survival
   After Revascularisation for Chronic Limb Threatening Ischaemia.* Eur J Vasc Endovasc Surg.
   2022 Jun 7;S1078-5884(22)00340-9. <https://doi.org/10.1016/j.ejvs.2022.05.038>

## Privacy

Patient data entered into the form stays in memory on the device. The app declares no network
permission, writes no patient data to storage, and sends nothing to a server. Closing the app
discards whatever was entered.

## Requirements

| | |
| --- | --- |
| Minimum Android version | 14 (API 34) |
| Target SDK | 36 |
| Compile SDK | 37 — required by AndroidX 2026.06; `targetSdk` stays at 36 because raising it opts into new runtime behaviour that needs device testing |
| JDK | 11 source/target compatibility |
| Android Gradle Plugin | 9.3.1 |
| Kotlin | 2.2.10 |
| Gradle | 9.5.0 (via the wrapper) |
| UI toolkit | Jetpack Compose, Material 3 (Compose BOM 2026.06.01) |

Dependency versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml); nothing is
declared with a literal version in `build.gradle.kts`.

## Building

```bash
./gradlew assembleDebug
```

Install onto a connected device or emulator:

```bash
./gradlew installDebug
```

Build the Play release bundle (output is written to `app/release`, which is git-ignored):

```bash
./gradlew bundleRelease
```

Release builds keep R8 optimization disabled (`optimization { enable = false }`) — the app is small
and shipping unobfuscated keeps crash reports readable.

The bundle also disables Play's per-language resource splitting. The in-app language switcher lets a
user pick Japanese or English regardless of the system locale, so every locale has to be present on
the device.

## Testing

Unit and Compose UI tests run on the JVM through Robolectric, so no device is needed:

```bash
./gradlew test
```

Instrumented tests, if you want them on a real device or emulator:

```bash
./gradlew connectedAndroidTest
```

The test suite covers the prediction maths ([`PatientRiskTest`](app/src/test/java/org/studiomexx/clitical_android/model/PatientRiskTest.kt)),
the form and its validation ([`QuestionFormTest`](app/src/test/java/org/studiomexx/clitical_android/ui/QuestionFormTest.kt)),
the results screen ([`ResultScreenTest`](app/src/test/java/org/studiomexx/clitical_android/ui/ResultScreenTest.kt)),
the settings and references screens ([`SettingsScreensTest`](app/src/test/java/org/studiomexx/clitical_android/ui/SettingsScreensTest.kt)),
and navigation ([`MainActivityTest`](app/src/test/java/org/studiomexx/clitical_android/MainActivityTest.kt)).
Robolectric resolves strings from the real resource table (`isIncludeAndroidResources = true`), so
tests can assert on localized text.

Development follows Red/Green TDD: add the failing test first, then the code that makes it pass.

## Project structure

```
app/src/main/java/org/studiomexx/clitical_android/
├── MainActivity.kt          Bottom-navigation shell; swaps in the result screen when a risk exists
├── model/
│   ├── PatientData.kt       Immutable data class holding one patient's inputs
│   ├── PatientRisk.kt       All five predictions, plus the published model coefficients
│   ├── Enums.kt             Sex, Activity, CKD, MalignantNeoplasm, Rutherford, risk levels
│   ├── Labeled.kt           Maps enum cases to string resources for the pickers
│   └── ValidationError.kt   Empty fields / no lesion selected
└── ui/
    ├── MainViewModel.kt     Form state, text buffers for numeric input, calculation entry point
    ├── QuestionForm.kt      The patient data form
    ├── ResultScreen.kt      Predicted risks
    ├── SettingsScreens.kt   Settings (language, terms, about) and References
    ├── SectionCard.kt       Shared grouped-list container
    ├── Utils.kt             localizedString(): resolves strings in the user-chosen locale
    └── theme/               Material 3 colour, typography, theme
```

`MainViewModel` keeps numeric input as text buffers separate from `PatientData` so a partially typed
value stays editable; the strings are parsed only when the user asks for a prediction.

## Localization

The app ships Japanese ([`values-ja/strings.xml`](app/src/main/res/values-ja/strings.xml)) and
English ([`values/strings.xml`](app/src/main/res/values/strings.xml)). The language is chosen in
Settings rather than following the system locale, so strings are resolved through the
`localizedString(id, locale)` helper instead of `stringResource`. When adding a string, add it to
both files.

## Versioning

`versionName` and `versionCode` live in [`app/build.gradle.kts`](app/build.gradle.kts). The Settings
screen shows the bare `versionName` via `BuildConfig`, so a release only needs the two values bumped
there. `versionCode` must increase for every build uploaded to Play.

## License

Released under the [MIT License](LICENSE).

© 2022 Kazuhiro Miyahara, Japanese Society for Vascular Surgery (JSVS), JCLIMB Committee.

The MIT License covers this source code. It does not extend to the published prediction models and
the papers they come from — see [References](#references) — nor does it waive the clinical
disclaimer above. Use of the distributed app is additionally governed by its
[terms of service](https://studiome.github.io/clti_risk/).
