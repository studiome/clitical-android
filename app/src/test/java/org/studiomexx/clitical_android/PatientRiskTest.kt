package org.studiomexx.clitical_android

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PatientRiskTest {

    @Test
    fun nullCase() {
        assertThrows(FormatException::class.java) {
            PatientRisk(PatientData())
        }
    }

    @Test
    fun normalCase() {
        val pd = PatientData().apply {
            age = 65
            weight = 50.0
            height = 1.50
            alb = 4.0
        }

        val want = Want(
            gnri = "101.3",
            gnriRisk = GNRIRisk.NO_RISK,
            predictedOS = "0.92",
            predictedAFS = "0.88",
            osRisk = OSRisk.LOW,
            predicted30DDorA = "0.013",
            predicted30DMALE = "0.032"
        )

        assertTestCase(pd, want)
    }

    @Test
    fun errorCase() {
        val pd = PatientData().apply {
            sex = Sex.MALE
            age = 70
            height = 0.0
            weight = 50.0
            alb = 3.0
            activity = Activity.WHEELCHAIR
        }

        val want = Want(
            gnri = "NaN",
            gnriRisk = null,
            predictedOS = "NaN",
            predictedAFS = "NaN",
            osRisk = null,
            predicted30DDorA = "NaN",
            predicted30DMALE = "NaN"
        )

        assertTestCase(pd, want)
    }

    @Test
    fun lowRiskCase() {
        val pd = PatientData().apply {
            sex = Sex.MALE
            age = 50
            height = 1.65
            weight = 60.0
            alb = 4.0
            activity = Activity.AMBULATORY
            hasCHF = false
            hasCVD = true
            ckd = CKD.G3
            malignant = MalignantNeoplasm.NO
            hasAILesion = false
            hasFPLesion = true
            hasBKLesion = false
            isUrgent = true
            hasFever = true
            hasAbnormalWBC = true
            hasLocalInfection = true
            hasCAD = true
            hasDyslipidemia = false
            isSmoking = true
            hasContraLateralLesion = false
            hasOtherVD = true
            rutherford = RutherfordClassification.CLASS4
        }

        val want = Want(
            gnri = "101.3",
            gnriRisk = GNRIRisk.NO_RISK,
            predictedOS = "0.91",
            predictedAFS = "0.64",
            osRisk = OSRisk.LOW,
            predicted30DDorA = "0.088",
            predicted30DMALE = "0.152"
        )

        assertTestCase(pd, want)
    }

    @Test
    fun mediumRiskCase() {
        val pd = PatientData().apply {
            sex = Sex.FEMALE
            age = 70
            height = 1.53
            weight = 55.0
            alb = 3.5
            activity = Activity.WHEELCHAIR
            hasCHF = true
            hasCVD = true
            ckd = CKD.G4
            malignant = MalignantNeoplasm.PAST_HISTORY
            hasAILesion = false
            hasFPLesion = true
            hasBKLesion = true
            isUrgent = true
            hasFever = true
            hasAbnormalWBC = true
            hasLocalInfection = true
            hasCAD = false
            hasDyslipidemia = true
            isSmoking = false
            hasContraLateralLesion = true
            hasOtherVD = false
            rutherford = RutherfordClassification.CLASS5
        }

        val want = Want(
            gnri = "93.8",
            gnriRisk = GNRIRisk.LOW,
            predictedOS = "0.67",
            predictedAFS = "0.25",
            osRisk = OSRisk.MEDIUM,
            predicted30DDorA = "0.170",
            predicted30DMALE = "0.175"
        )

        assertTestCase(pd, want)
    }

    @Test
    fun highRiskCase() {
        val pd = PatientData().apply {
            sex = Sex.MALE
            age = 85
            height = 1.75
            weight = 55.1
            alb = 3.5
            activity = Activity.IMMOBILE
            hasCHF = false
            hasCVD = false
            ckd = CKD.G5
            malignant = MalignantNeoplasm.UNDER_TREATMENT
            hasAILesion = false
            hasFPLesion = false
            hasBKLesion = true
            isUrgent = true
            hasFever = false
            hasAbnormalWBC = true
            hasLocalInfection = false
            hasCAD = true
            hasDyslipidemia = true
            isSmoking = true
            hasContraLateralLesion = true
            hasOtherVD = false
            rutherford = RutherfordClassification.CLASS5
        }

        val want = Want(
            gnri = "86.2",
            gnriRisk = GNRIRisk.MODERATE,
            predictedOS = "0.08",
            predictedAFS = "0.03",
            osRisk = OSRisk.HIGH,
            predicted30DDorA = "0.100",
            predicted30DMALE = "0.043"
        )

        assertTestCase(pd, want)
    }

    @Test
    fun highRiskCase2() {
        val pd = PatientData().apply {
            sex = Sex.FEMALE
            age = 90
            height = 1.55
            weight = 30.0
            alb = 3.2
            activity = Activity.IMMOBILE
            hasCHF = true
            hasCVD = true
            ckd = CKD.G5D
            malignant = MalignantNeoplasm.UNDER_TREATMENT
            hasAILesion = false
            hasFPLesion = false
            hasBKLesion = true
            isUrgent = true
            hasFever = true
            hasAbnormalWBC = true
            hasLocalInfection = true
            hasCAD = true
            hasDyslipidemia = true
            isSmoking = true
            hasContraLateralLesion = false
            hasOtherVD = true
            rutherford = RutherfordClassification.CLASS6
        }

        val want = Want(
            gnri = "71.3",
            gnriRisk = GNRIRisk.MAJOR,
            predictedOS = "0.00",
            predictedAFS = "0.00",
            osRisk = OSRisk.HIGH,
            predicted30DDorA = "0.370",
            predicted30DMALE = "0.122"
        )

        assertTestCase(pd, want)
    }

    private data class Want(
        val gnri: String,
        val gnriRisk: GNRIRisk?,
        val predictedOS: String,
        val predictedAFS: String,
        val osRisk: OSRisk?,
        val predicted30DDorA: String,
        val predicted30DMALE: String
    )

    private fun assertTestCase(pd: PatientData, want: Want) {
        val pr = PatientRisk(pd)
        
        assertEquals(want.gnri, formatDouble(pr.gnri, 1))
        assertEquals(want.gnriRisk, pr.gnriRisk)
        assertEquals(want.predictedOS, formatDouble(pr.predictedOS, 2))
        assertEquals(want.predictedAFS, formatDouble(pr.predictedAFS, 2))
        assertEquals(want.osRisk, pr.osRisk)
        assertEquals(want.predicted30DDorA, formatDouble(pr.predicted30DDeathOrAmputation, 3))
        assertEquals(want.predicted30DMALE, formatDouble(pr.predicted30DMALE, 3))
    }

    private fun formatDouble(v: Double, fractionDigits: Int): String {
        if (v.isNaN()) return "NaN"
        return String.format(java.util.Locale.US, "%.${fractionDigits}f", v)
    }
}
