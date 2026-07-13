package org.studiomexx.clitical_android

class PatientData {
    var sex: Sex = Sex.FEMALE
    var age: Int? = null
    var weight: Double? = null // kg
    var height: Double? = null // m
    var alb: Double? = null // g/dl
    var activity: Activity = Activity.AMBULATORY

    var hasCHF: Boolean = false
    var hasCVD: Boolean = false
    var ckd: CKD = CKD.NORMAL
    var malignant: MalignantNeoplasm = MalignantNeoplasm.NO

    var hasAILesion: Boolean = true
    var hasFPLesion: Boolean = false
    var hasBKLesion: Boolean = false

    var isUrgent: Boolean = false
    var hasFever: Boolean = false
    var hasAbnormalWBC: Boolean = false
    var hasLocalInfection: Boolean = false
    var hasDyslipidemia: Boolean = false
    var isSmoking: Boolean = false
    var hasCAD: Boolean = false
    var hasContraLateralLesion: Boolean = false
    var hasOtherVD: Boolean = false
    var rutherford: RutherfordClassification = RutherfordClassification.CLASS4

    fun reset() {
        sex = Sex.FEMALE
        age = null
        weight = null
        height = null
        alb = null
        activity = Activity.AMBULATORY
        hasCHF = false
        hasCVD = false
        ckd = CKD.NORMAL
        malignant = MalignantNeoplasm.NO
        hasAILesion = true
        hasFPLesion = false
        hasBKLesion = false
        isUrgent = false
        hasFever = false
        hasAbnormalWBC = false
        hasLocalInfection = false
        hasDyslipidemia = false
        isSmoking = false
        hasCAD = false
        hasContraLateralLesion = false
        hasOtherVD = false
        rutherford = RutherfordClassification.CLASS4
    }
}
