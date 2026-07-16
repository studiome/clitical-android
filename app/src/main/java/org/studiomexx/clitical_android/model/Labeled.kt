package org.studiomexx.clitical_android.model

import androidx.annotation.StringRes

interface Labeled {
    @get:StringRes
    val stringResId: Int
}
