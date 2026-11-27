package org.studiomexx.clitical_android.model

import androidx.annotation.StringRes
import org.studiomexx.clitical_android.R

enum class Sex(@get:StringRes override val stringResId: Int) : Labelled {
    MALE(R.string.male),
    FEMALE(R.string.female)
}

enum class Activity(@get:StringRes override val stringResId: Int) : Labelled {
    AMBULATORY(R.string.ambulatory),
    WHEELCHAIR(R.string.wheelchair),
    IMMOBILE(R.string.immobile)
}

enum class CKD(@get:StringRes override val stringResId: Int) : Labelled {
    NORMAL(R.string.normal),
    G3(R.string.g3),
    G4(R.string.g4),
    G5(R.string.g5),
    G5D(R.string.g5D)
}

enum class MalignantNeoplasm(@get:StringRes override val stringResId: Int) : Labelled {
    NO(R.string.noMalignancy),
    PAST_HISTORY(R.string.pastHistory),
    UNDER_TREATMENT(R.string.underTreatment)
}

enum class RutherfordClassification(@get:StringRes override val stringResId: Int) : Labelled {
    CLASS4(R.string.class4),
    CLASS5(R.string.class5),
    CLASS6(R.string.class6)
}

enum class GNRIRisk(@get:StringRes override val stringResId: Int) : Labelled {
    MAJOR(R.string.gnriMajorRisk),
    MODERATE(R.string.gnriModerateRisk),
    LOW(R.string.gnriLowRisk),
    NO_RISK(R.string.gnriNoRisk)
}

enum class OSRisk(@get:StringRes override val stringResId: Int) : Labelled {
    HIGH(R.string.osHighRisk),
    MEDIUM(R.string.osMediumRisk),
    LOW(R.string.osLowRisk)
}
