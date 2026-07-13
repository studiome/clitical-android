package org.studiomexx.clitical_android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Locale

class MainViewModel : ViewModel() {
    var locale by mutableStateOf(Locale("ja"))

    // Patient input states
    var sex by mutableStateOf(Sex.FEMALE)
    var ageText by mutableStateOf("")
    var heightText by mutableStateOf("")
    var weightText by mutableStateOf("")
    var albText by mutableStateOf("")
    var activity by mutableStateOf(Activity.AMBULATORY)

    var hasCHF by mutableStateOf(false)
    var hasCVD by mutableStateOf(false)
    var ckd by mutableStateOf(CKD.NORMAL)
    var malignant by mutableStateOf(MalignantNeoplasm.NO)

    var hasAILesion by mutableStateOf(true)
    var hasFPLesion by mutableStateOf(false)
    var hasBKLesion by mutableStateOf(false)

    var isUrgent by mutableStateOf(false)
    var hasFever by mutableStateOf(false)
    var hasAbnormalWBC by mutableStateOf(false)
    var hasLocalInfection by mutableStateOf(false)
    var hasDyslipidemia by mutableStateOf(false)
    var isSmoking by mutableStateOf(false)
    var hasCAD by mutableStateOf(false)
    var hasContraLateralLesion by mutableStateOf(false)
    var hasOtherVD by mutableStateOf(false)
    var rutherford by mutableStateOf(RutherfordClassification.CLASS4)

    // Result state
    var calculatedRisk by mutableStateOf<PatientRisk?>(null)

    fun reset() {
        sex = Sex.FEMALE
        ageText = ""
        heightText = ""
        weightText = ""
        albText = ""
        activity = Activity.AMBULATORY
        hasCHF = false
        hasCVD = false
        ckd = CKD.NORMAL
        malignant = MalignantNeoplasm.NO
        hasAILesion = true
        hasFPLesion = false
        hasBKLesion = false
        isUrgent = false
        hasFever = false
        hasAbnormalWBC = false
        hasLocalInfection = false
        hasDyslipidemia = false
        isSmoking = false
        hasCAD = false
        hasContraLateralLesion = false
        hasOtherVD = false
        rutherford = RutherfordClassification.CLASS4
        calculatedRisk = null
    }

    fun calculateRisk(): String? {
        val age = ageText.toIntOrNull()
        val heightCM = heightText.toDoubleOrNull()
        val height = if (heightCM != null) heightCM / 100.0 else null
        val weight = weightText.toDoubleOrNull()
        val alb = albText.toDoubleOrNull()

        val data = PatientData().apply {
            this.sex = this@MainViewModel.sex
            this.age = age
            this.height = height
            this.weight = weight
            this.alb = alb
            this.activity = this@MainViewModel.activity
            this.hasCHF = this@MainViewModel.hasCHF
            this.hasCVD = this@MainViewModel.hasCVD
            this.ckd = this@MainViewModel.ckd
            this.malignant = this@MainViewModel.malignant
            this.hasAILesion = this@MainViewModel.hasAILesion
            this.hasFPLesion = this@MainViewModel.hasFPLesion
            this.hasBKLesion = this@MainViewModel.hasBKLesion
            this.isUrgent = this@MainViewModel.isUrgent
            this.hasFever = this@MainViewModel.hasFever
            this.hasAbnormalWBC = this@MainViewModel.hasAbnormalWBC
            this.hasLocalInfection = this@MainViewModel.hasLocalInfection
            this.hasDyslipidemia = this@MainViewModel.hasDyslipidemia
            this.isSmoking = this@MainViewModel.isSmoking
            this.hasCAD = this@MainViewModel.hasCAD
            this.hasContraLateralLesion = this@MainViewModel.hasContraLateralLesion
            this.hasOtherVD = this@MainViewModel.hasOtherVD
            this.rutherford = this@MainViewModel.rutherford
        }

        try {
            calculatedRisk = PatientRisk(data)
            return null
        } catch (e: FormatException) {
            calculatedRisk = null
            return e.source
        } catch (e: Exception) {
            calculatedRisk = null
            return "DefaultError"
        }
    }
}
