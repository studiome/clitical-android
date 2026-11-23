package org.studiomexx.clitical_android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.studiomexx.clitical_android.model.FormatException
import org.studiomexx.clitical_android.model.PatientData
import org.studiomexx.clitical_android.model.PatientRisk
import org.studiomexx.clitical_android.model.ValidationError
import java.util.Locale
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class MainViewModel : ViewModel() {
    var locale: Locale by mutableStateOf(Locale.forLanguageTag("ja"))

    var calculatedRisk by mutableStateOf<PatientRisk?>(null)

    /**
     * Input state whose edits discard [calculatedRisk]. On a wide window the result sits
     * beside the form, and it must never be shown next to data it wasn't computed from.
     */
    private fun <T> input(initial: T) = object : ReadWriteProperty<Any?, T> {
        var state by mutableStateOf(initial)
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = state
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (value != state) calculatedRisk = null
            state = value
        }
    }

    // Text buffers for numeric inputs, kept separate so partially-typed values can be edited freely
    var ageText by input("")
    var heightText by input("")
    var weightText by input("")
    var albText by input("")

    var patientData by input(PatientData())
        private set

    /** True after the user attempts analysis; keeps field validation visible until fixed. */
    var hasSubmitted by mutableStateOf(false)
    var validationAttempt by mutableIntStateOf(0)

    fun updatePatientData(transform: (PatientData) -> PatientData) {
        patientData = transform(patientData)
    }

    fun reset() {
        ageText = ""
        heightText = ""
        weightText = ""
        albText = ""
        patientData = PatientData()
        calculatedRisk = null
        hasSubmitted = false
        validationAttempt = 0
    }

    fun calculateRisk(): ValidationError? {
        hasSubmitted = true
        validationAttempt++
        val data = patientData.copy(
            age = ageText.toIntOrNull(),
            height = heightText.toDoubleOrNull()?.div(100.0),
            weight = weightText.toDoubleOrNull(),
            alb = albText.toDoubleOrNull()
        )

        return try {
            calculatedRisk = PatientRisk(data)
            null
        } catch (e: FormatException) {
            calculatedRisk = null
            e.source
        }
    }
}
