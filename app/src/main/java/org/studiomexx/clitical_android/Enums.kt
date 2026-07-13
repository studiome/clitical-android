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
