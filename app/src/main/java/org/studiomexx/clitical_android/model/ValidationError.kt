package org.studiomexx.clitical_android.model

enum class ValidationError {
    EMPTY_FIELDS,
    NO_LESION_SELECTED
}

class FormatException(message: String, val source: ValidationError) : Exception(message)
