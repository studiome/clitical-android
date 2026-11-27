package org.studiomexx.clitical_android.model

data class PatientData(
    val sex: Sex = Sex.FEMALE,
    val age: Int? = null,
    val weight: Double? = null, // kg
    val height: Double? = null, // m
    val alb: Double? = null, // g/dl
    val activity: Activity = Activity.AMBULATORY,

    val hasCHF: Boolean = false,
    val hasCVD: Boolean = false,
    val ckd: CKD = CKD.NORMAL,
    val malignant: MalignantNeoplasm = MalignantNeoplasm.NO,

    val hasAILesion: Boolean = true,
    val hasFPLesion: Boolean = false,
    val hasBKLesion: Boolean = false,

    val isUrgent: Boolean = false,
    val hasFever: Boolean = false,
    val hasAbnormalWBC: Boolean = false,
    val hasLocalInfection: Boolean = false,
    val hasDyslipidemia: Boolean = false,
    val isSmoking: Boolean = false,
    val hasCAD: Boolean = false,
    val hasContraLateralLesion: Boolean = false,
    val hasOtherVD: Boolean = false,
    val rutherford: RutherfordClassification = RutherfordClassification.CLASS4,
) {
    companion object {
        // Plausible clinical values, matching the iOS app. Anything outside is far more likely
        // a typo or a wrong unit than a patient, and would give a plausible-looking wrong risk.
        val validAgeRange = 18..120

        /** In metres like [height]; the form's field is in cm (100..250). */
        val validHeightRange = 1.0..2.5
        val validWeightRange = 20.0..300.0
        val validAlbRange = 1.0..6.0
    }
}

/** The first problem that prevents a risk calculation, or null when the data is valid. */
fun PatientData.validate(): ValidationError? {
    if (age == null || height == null || weight == null || alb == null) {
        return ValidationError.EMPTY_FIELDS
    }
    if (age !in PatientData.validAgeRange ||
        height !in PatientData.validHeightRange ||
        weight !in PatientData.validWeightRange ||
        alb !in PatientData.validAlbRange
    ) {
        return ValidationError.OUT_OF_RANGE
    }
    if (!hasAILesion && !hasFPLesion && !hasBKLesion) return ValidationError.NO_LESION_SELECTED
    return null
}
