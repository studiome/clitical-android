package org.studiomexx.clitical_android.model

enum class ValidationError {
    EMPTY_FIELDS,
    NO_LESION_SELECTED,
    OUT_OF_RANGE
}

class FormatException(message: String, val source: ValidationError) : Exception(message)
