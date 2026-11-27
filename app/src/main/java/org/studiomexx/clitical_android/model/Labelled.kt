package org.studiomexx.clitical_android.model

import androidx.annotation.StringRes

interface Labelled {
    @get:StringRes
    val stringResId: Int
}
