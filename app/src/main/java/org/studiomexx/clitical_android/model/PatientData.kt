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
    val rutherford: RutherfordClassification = RutherfordClassification.CLASS4
)
