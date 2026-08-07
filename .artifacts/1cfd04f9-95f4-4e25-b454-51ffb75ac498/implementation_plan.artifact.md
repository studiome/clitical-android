# 修正プラン：PatientRisk.kt 等の警告解決

プロジェクト内のモデルクラスにおける警告（Warning）を解決します。

## 変更内容

### [PatientRisk.kt](file:///Users/kmiyahara/work/CLiTICALAndroid/app/src/main/java/org/studiomexx/clitical_android/model/PatientRisk.kt)

1.  **コンストラクタ引数の修正**: `patientData` プロパティがインスタンス変数として使用されていない（初期化時のみ使用）ため、`val` を削除します。
2.  **`when` 式の改善**: `calcSigma` メソッド内の複数の `when` ブロックで、代入操作（`sigma += ...`）を `when` の外に括り出します。
3.  **定数名の命名規則修正**: `osH0Coeff` と `afsH0Coeff` を `OS_H0_COEFF` と `AFS_H0_COEFF` に改名します。

### [PatientData.kt](file:///Users/kmiyahara/work/CLiTICALAndroid/app/src/main/java/org/studiomexx/clitical_android/model/PatientData.kt)

1.  **末尾カンマの追加**: コンストラクタ引数の末尾にカンマを追加します。

### [Enums.kt](file:///Users/kmiyahara/work/CLiTICALAndroid/app/src/main/java/org/studiomexx/clitical_android/model/Enums.kt)

1.  **アノテーションのターゲット指定**: `@StringRes` に `@get:` ターゲットを追加し、Kotlin 1.9 以降での曖昧さを解消します。

## 検証計画

- Gradle ビルドを実行し、ビルドが成功することを確認します。
- 静的解析を再実行し、警告が消えていることを確認します。
