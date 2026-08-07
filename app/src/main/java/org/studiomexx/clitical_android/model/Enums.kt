package org.studiomexx.clitical_android.model

import androidx.annotation.StringRes
import org.studiomexx.clitical_android.R

enum class Sex(@get:StringRes override val stringResId: Int) : Labeled {
    MALE(R.string.male),
    FEMALE(R.string.female)
}

enum class Activity(@get:StringRes override val stringResId: Int) : Labeled {
    AMBULATORY(R.string.ambulatory),
    WHEELCHAIR(R.string.wheelchair),
    IMMOBILE(R.string.immobile)
}

enum class CKD(@get:StringRes override val stringResId: Int) : Labeled {
    NORMAL(R.string.normal),
    G3(R.string.g3),
    G4(R.string.g4),
    G5(R.string.g5),
    G5D(R.string.g5D)
}

enum class MalignantNeoplasm(@get:StringRes override val stringResId: Int) : Labeled {
    NO(R.string.noMalignancy),
    PAST_HISTORY(R.string.pastHistory),
    UNDER_TREATMENT(R.string.underTreatment)
}

enum class RutherfordClassification(@get:StringRes override val stringResId: Int) : Labeled {
    CLASS4(R.string.class4),
    CLASS5(R.string.class5),
    CLASS6(R.string.class6)
}

enum class GNRIRisk(@get:StringRes override val stringResId: Int) : Labeled {
    MAJOR(R.string.gntiMajorRisk),
    MODERATE(R.string.gnriModerateRisk),
    LOW(R.string.gnriLowRisk),
    NO_RISK(R.string.gnriNoRisk)
}

enum class OSRisk(@get:StringRes override val stringResId: Int) : Labeled {
    HIGH(R.string.osHighRisk),
    MEDIUM(R.string.osMediumRisk),
    LOW(R.string.osLowRisk)
}
