# CLiTICAL for Android

*[English README](README.md)*

CLiTICAL は、血行再建術の予定されている**包括的慢性下肢虚血 (CLTI)** 患者のリスクを予測するアプリです。
年齢・性別、栄養状態、併存疾患、病変分布、Rutherford 分類といった日常診療で得られる情報から、周術期および
中期の予後を予測します。予測モデルは日本血管外科学会 (JSVS) の JCLIMB レジストリの解析に基づいています。

本リポジトリは Android 版です。iOS 版 (`clitical-ios`) が別に存在し、同じ情報を同じ順序で提示できるよう、
Android の UI は意図的に iOS 版の構成に合わせています。

> [!IMPORTANT]
> 予測値は臨床判断を補助する参考情報であり、診断や治療方針の決定に代わるものではありません。
> 最終的な判断は担当医の責任において行ってください。

## 予測項目

| 出力 | 内容 |
| --- | --- |
| **GNRI** | Geriatric Nutritional Risk Index。栄養リスクを 4 段階（リスクなし／軽度／中等度／高度）に分類 |
| **予測30日死亡・大切断率** | 血行再建後 30 日以内の死亡もしくは大切断の予測リスク |
| **予測30日MALE発生率** | 血行再建後 30 日以内の主要有害下肢事故（患肢の足関節より近位での切断、または大きな再介入）の予測リスク |
| **予測2年OS** | 血行再建後 2 年の全生存率。低／中等度／高リスクに分類 |
| **予測2年AFS** | 血行再建後 2 年の大切断回避生存率 |

GNRI は先に算出され、生存予測モデルの共変量として使われます。したがって栄養状態はすべての予測に影響します。

## 画面構成

ボトムナビゲーションで 3 つの画面を切り替えます。

- **リスク評価** — 患者データ入力フォーム（基本情報・生活歴・臨床情報・血行再建対象病変・その他血管病変・
  合併症）と、そこから遷移する結果画面。
- **参考文献** — 出典論文 2 件。Custom Tab で開きます。
- **設定** — 言語切り替え、利用規約、アプリ情報。

計算前に入力値を検証します。数値項目がすべて埋まっていること、動脈病変部位が最低 1 つ選択されていることが
条件で、満たさない場合は数値を出さずに該当する不備を表示します。

## 参考文献

1. Miyata T. et al. *Risk prediction model for early outcomes of revascularization for chronic
   limb-threatening ischaemia.* Br J Surg. 2022 Oct 14;109(11):1123.
   <https://doi.org/10.1093/bjs/znab036>
2. Miyata T. et al. *Prediction Models for Two Year Overall Survival and Amputation Free Survival
   After Revascularisation for Chronic Limb Threatening Ischaemia.* Eur J Vasc Endovasc Surg.
   2022 Jun 7;S1078-5884(22)00340-9. <https://doi.org/10.1016/j.ejvs.2022.05.038>

## プライバシー

フォームに入力された患者データは端末のメモリ上でのみ扱われます。アプリはネットワーク権限を宣言しておらず、
患者データをストレージに書き出すことも、サーバーへ送信することもありません。アプリを終了すると入力内容は
破棄されます。

## 動作環境・開発環境

| | |
| --- | --- |
| 最小 Android バージョン | 14 (API 34) |
| target SDK | 36 |
| compile SDK | 37 — AndroidX 2026.06 が要求。`targetSdk` は 36 のまま（引き上げると新しいランタイム挙動が有効になり実機検証が必要なため） |
| JDK | ソース／ターゲット互換性 11 |
| Android Gradle Plugin | 9.3.1 |
| Kotlin | 2.2.10 |
| Gradle | 9.5.0（wrapper 経由） |
| UI | Jetpack Compose / Material 3（Compose BOM 2026.06.01） |

依存関係のバージョンは [`gradle/libs.versions.toml`](gradle/libs.versions.toml) に集約しており、
`build.gradle.kts` にバージョンを直書きしていません。

## ビルド

```bash
./gradlew assembleDebug
```

接続中の実機・エミュレータへのインストール:

```bash
./gradlew installDebug
```

Play 用リリースバンドルの作成（出力先の `app/release` は git 管理外）:

```bash
./gradlew bundleRelease
```

リリースビルドでは R8 の最適化を無効にしています (`optimization { enable = false }`)。アプリが小規模であり、
難読化しないほうがクラッシュレポートを読みやすいためです。

またバンドルでは Play の言語別リソース分割を無効化しています。アプリ内の言語切り替えはシステムロケールに
関係なく日本語／英語を選べる必要があり、全ロケールが端末上に存在していなければならないからです。

## テスト

ユニットテストと Compose UI テストは Robolectric により JVM 上で実行されるため、実機は不要です。

```bash
./gradlew test
```

実機・エミュレータで計装テストを実行する場合:

```bash
./gradlew connectedAndroidTest
```

テストは予測計算 ([`PatientRiskTest`](app/src/test/java/org/studiomexx/clitical_android/model/PatientRiskTest.kt))、
フォームと入力検証 ([`QuestionFormTest`](app/src/test/java/org/studiomexx/clitical_android/ui/QuestionFormTest.kt))、
結果画面 ([`ResultScreenTest`](app/src/test/java/org/studiomexx/clitical_android/ui/ResultScreenTest.kt))、
設定・参考文献画面 ([`SettingsScreensTest`](app/src/test/java/org/studiomexx/clitical_android/ui/SettingsScreensTest.kt))、
画面遷移 ([`MainActivityTest`](app/src/test/java/org/studiomexx/clitical_android/MainActivityTest.kt)) を対象と
しています。Robolectric は実際のリソーステーブルから文字列を解決するため (`isIncludeAndroidResources = true`)、
ローカライズされた文言をそのままアサートできます。

開発は Red/Green TDD で進めます。先に失敗するテストを追加し、その後それを通すコードを書きます。

## ディレクトリ構成

```
app/src/main/java/org/studiomexx/clitical_android/
├── MainActivity.kt          ボトムナビゲーションの土台。リスク算出時は結果画面に差し替える
├── model/
│   ├── PatientData.kt       患者 1 人分の入力を保持するイミュータブルなデータクラス
│   ├── PatientRisk.kt       5 つの予測値と、論文由来のモデル係数
│   ├── Enums.kt             性別・ADL・CKD・悪性新生物・Rutherford・リスク区分
│   ├── Labeled.kt           enum と文字列リソースの対応（ピッカー表示用）
│   └── ValidationError.kt   未入力／病変部位未選択
└── ui/
    ├── MainViewModel.kt     フォーム状態、数値入力のテキストバッファ、計算の入口
    ├── QuestionForm.kt      患者データ入力フォーム
    ├── ResultScreen.kt      予測リスク表示
    ├── SettingsScreens.kt   設定（言語・利用規約・アプリ情報）と参考文献
    ├── SectionCard.kt       共通のグループ化リストコンテナ
    ├── Utils.kt             localizedString(): 選択中のロケールで文字列を解決
    └── theme/               Material 3 のカラー・タイポグラフィ・テーマ
```

`MainViewModel` は数値入力を `PatientData` とは別のテキストバッファとして保持します。入力途中の値を自由に
編集できるようにするためで、文字列のパースは予測実行時にのみ行います。

## ローカライズ

日本語 ([`values-ja/strings.xml`](app/src/main/res/values-ja/strings.xml)) と英語
([`values/strings.xml`](app/src/main/res/values/strings.xml)) を同梱しています。言語はシステムロケールでは
なく設定画面で選ぶため、文字列は `stringResource` ではなく `localizedString(id, locale)` ヘルパーで解決します。
文字列を追加する際は両方のファイルに追加してください。

## バージョン管理

`versionName` と `versionCode` は [`app/build.gradle.kts`](app/build.gradle.kts) にあります。設定画面は
`BuildConfig` 経由で `versionName` をそのまま表示するため、リリース時はこの 2 つを更新するだけで済みます。
Play にアップロードするビルドごとに `versionCode` を増やす必要があります。

## ライセンス

© 2022 宮原和洋、特定非営利活動法人日本血管外科学会 (JSVS)、JCLIMB 委員会

アプリの利用は[利用規約](https://studiome.github.io/clti_risk/)に従います。本リポジトリにはオープンソース
ライセンスは設定されていません。
