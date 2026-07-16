package org.studiomexx.clitical_android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.studiomexx.clitical_android.model.FormatException
import org.studiomexx.clitical_android.model.PatientData
import org.studiomexx.clitical_android.model.PatientRisk
import org.studiomexx.clitical_android.model.ValidationError
import java.util.Locale

class MainViewModel : ViewModel() {
    var locale by mutableStateOf(Locale.forLanguageTag("ja"))

    // Text buffers for numeric inputs, kept separate so partially-typed values can be edited freely
    var ageText by mutableStateOf("")
    var heightText by mutableStateOf("")
    var weightText by mutableStateOf("")
    var albText by mutableStateOf("")

    var patientData by mutableStateOf(PatientData())
        private set

    var calculatedRisk by mutableStateOf<PatientRisk?>(null)

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
    }

    fun calculateRisk(): ValidationError? {
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
