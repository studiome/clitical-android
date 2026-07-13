package org.studiomexx.clitical_android

enum class Sex(val stringResId: Int) {
    MALE(R.string.male),
    FEMALE(R.string.female)
}

enum class Activity(val stringResId: Int) {
    AMBULATORY(R.string.ambulatory),
    WHEELCHAIR(R.string.wheelchair),
    IMMOBILE(R.string.immobile)
}

enum class CKD(val stringResId: Int) {
    NORMAL(R.string.normal),
    G3(R.string.g3),
    G4(R.string.g4),
    G5(R.string.g5),
    G5D(R.string.g5D)
}

enum class MalignantNeoplasm(val stringResId: Int) {
    NO(R.string.noMalignancy),
    PAST_HISTORY(R.string.pastHistory),
    UNDER_TREATMENT(R.string.underTreatment)
}

enum class RutherfordClassification(val stringResId: Int) {
    CLASS4(R.string.class4),
    CLASS5(R.string.class5),
    CLASS6(R.string.class6)
}

enum class GNRIRisk(val stringResId: Int) {
    MAJOR(R.string.gntiMajorRisk),
    MODERATE(R.string.gnriModerateRisk),
    LOW(R.string.gnriLowRisk),
    NO_RISK(R.string.gnriNoRisk)
}

enum class OSRisk(val stringResId: Int) {
    HIGH(R.string.osHighRisk),
    MEDIUM(R.string.osMediumRisk),
    LOW(R.string.osLowRisk)
}

enum class Questions(val titleResId: Int, val subtitleResId: Int) {
    INSTRUCTION(R.string.questionInstructionTitle, R.string.questionInstructionSubtitle),
    SEX(R.string.questionSexTitle, R.string.questionSexSubtitle),
    AGE(R.string.questionAgeTitle, R.string.questionAgeSubtitle),
    HEIGHT(R.string.questionHeightTitle, R.string.questionHeightSubtitle),
    WEIGHT(R.string.questionWeightTitle, R.string.questionWeightSubtitle),
    ALBUMIN(R.string.questionAlbTitle, R.string.questionAlbSubtitle),
    ACTIVITY(R.string.questionActivityTitle, R.string.questionActivitySubtitle),
    CHF(R.string.questionCHFTitle, R.string.questionCHFSubtitle),
    CAD(R.string.questionCADTitle, R.string.questionCADSubtitle),
    CVD(R.string.questionCVDTitle, R.string.questionCVDSubtitle),
    CKD(R.string.questionCKDTitle, R.string.questionCKDSubtitle),
    MALIGNANT_NEOPLASM(R.string.questionMalignantTitle, R.string.questionMalignantSubtitle),
    LESION_AI(R.string.questionAILesionTitle, R.string.questionAILesionSubtitle),
    LESION_FP(R.string.questionFPLesionTitle, R.string.questionFPLesionSubtitle),
    LESION_BK(R.string.questionBKLesionTitle, R.string.questionBKLesionSubtitle),
    URGENT_PROCEDURE(R.string.questionUrgentTitle, R.string.questionUrgentSubtitle),
    FEVER(R.string.questionFeverTitle, R.string.questionFeverSubtitle),
    ABNORMAL_WBC(R.string.questionAbnormalWBCTitle, R.string.questionAbnormalWBCSubtitle),
    LOCAL_INFECTION(R.string.questionLocalInfectionTitle, R.string.questionLocalInfectionSubtitle),
    DYSLIPIDEMIA(R.string.questionDLTitle, R.string.questionDLSubtitle),
    SMOKING(R.string.questionSmokingTitle, R.string.questionSmokingSubtitle),
    CONTRALATERAL(R.string.questionContraTitle, R.string.questionContraSubtitle),
    OTHERS(R.string.questionOtherLesionTitle, R.string.questionOtherLesionSubtitle),
    RUTHERFORD(R.string.questionRutherfordTitle, R.string.questionRutherfordSubtitle),
    SUMMARY(R.string.questionSummaryTitle, R.string.questionSummarySubtitle)
}
