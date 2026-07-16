package org.studiomexx.clitical_android.model

import kotlin.math.exp
import kotlin.math.pow

class PatientRisk(val patientData: PatientData) {

    init {
        if (patientData.weight == null ||
            patientData.height == null ||
            patientData.age == null ||
            patientData.alb == null
        ) {
            throw FormatException("form is empty", ValidationError.EMPTY_FIELDS)
        }
        if (!patientData.hasAILesion &&
            !patientData.hasFPLesion &&
            !patientData.hasBKLesion
        ) {
            throw FormatException("wrong lesion choice", ValidationError.NO_LESION_SELECTED)
        }
    }

    val gnri: Double = calcGNRI(patientData)
    val gnriRisk: GNRIRisk? = classifyGNRIRisk(gnri)
    val predictedOS: Double = calcPredictedOS(patientData)
    val predictedAFS: Double = calcPredictedAFS(patientData)
    val osRisk: OSRisk? = classifyOSRisk(predictedOS)
    val predicted30DDeathOrAmputation: Double = calc30DDorA(patientData)
    val predicted30DMALE: Double = calc30DMALE(patientData)

    private fun calcGNRI(data: PatientData): Double {
        val height = data.height ?: return Double.NaN
        val weight = data.weight ?: return Double.NaN
        val alb = data.alb ?: return Double.NaN
        if (height == 0.0) return Double.NaN
        var wi = weight / (22.0 * height.pow(2.0))
        if (wi >= 1.0) wi = 1.0
        return 14.89 * alb + 41.7 * wi
    }

    private fun classifyGNRIRisk(gnri: Double): GNRIRisk? {
        if (gnri.isNaN()) return null
        return when {
            gnri >= 98.0 -> GNRIRisk.NO_RISK
            gnri >= 92.0 -> GNRIRisk.LOW
            gnri >= 82.0 -> GNRIRisk.MODERATE
            else -> GNRIRisk.MAJOR
        }
    }

    private fun calcPredictedOS(data: PatientData): Double {
        val sigma = calcSigma(data, osCoeff)
        if (sigma.isNaN()) return Double.NaN
        return osH0Coeff.pow(exp(sigma))
    }

    private fun calcPredictedAFS(data: PatientData): Double {
        val sigma = calcSigma(data, afsCoeff)
        if (sigma.isNaN()) return Double.NaN
        return afsH0Coeff.pow(exp(sigma))
    }

    private fun classifyOSRisk(overallSurvival: Double): OSRisk? {
        if (overallSurvival.isNaN()) return null
        return when {
            overallSurvival >= 0.70 -> OSRisk.LOW
            overallSurvival >= 0.50 -> OSRisk.MEDIUM
            else -> OSRisk.HIGH
        }
    }

    private fun calc30DDorA(data: PatientData): Double {
        val sigma = calcSigma(data, shortDeadOrAmputationCoeff)
        if (sigma.isNaN()) return Double.NaN
        return 1.0 / (1.0 + exp(sigma))
    }

    private fun calc30DMALE(data: PatientData): Double {
        val sigma = calcSigma(data, shortMALECoeff)
        if (sigma.isNaN()) return Double.NaN
        return 1.0 / (1.0 + exp(sigma))
    }

    // Covariants mapping helper
    enum class Covariants {
        IS_FEMALE,
        AGE_65_TO_74,
        AGE_75_TO_84,
        AGE_OVER_85,
        HAS_CHF,
        HAS_CVD,
        HAS_CKD_G3,
        HAS_CKD_G4,
        HAS_CKD_G5,
        HAS_CKD_G5D,
        GNRI_NO_OR_LOW,
        GNRI_MODERATE,
        GNRI_MAJOR,
        ACTIVITY_AMBULATORY,
        ACTIVITY_WHEELCHAIR,
        ACTIVITY_IMMOBILE,
        PAST_MALIGNANCY,
        TREATING_MALIGNANCY,
        IS_URGENT,
        FEVER,
        ABNORMAL_WBC,
        LOCAL_INFECTION,
        HAS_CAD,
        IS_SMOKING,
        HAS_DYSLIPIDEMIA,
        HAS_NO_AI_LESION,
        HAS_NO_FP_LESION,
        LESION_FP,
        LESION_BELOW_IP,
        HAS_NO_CONTRALATERAL,
        HAS_OTHER,
        RUTHERFORD_4,
        RUTHERFORD_5,
        RUTHERFORD_6,
        INTERCEPT
    }

    private fun calcSigma(data: PatientData, coeff: Map<Covariants, Double>): Double {
        var sigma = 0.0
        val age = data.age ?: return Double.NaN

        // sex
        if (data.sex == Sex.FEMALE) sigma += coeff[Covariants.IS_FEMALE] ?: 0.0

        // age
        when {
            age >= 85 -> sigma += coeff[Covariants.AGE_OVER_85] ?: 0.0
            age >= 75 -> sigma += coeff[Covariants.AGE_75_TO_84] ?: 0.0
            age >= 65 -> sigma += coeff[Covariants.AGE_65_TO_74] ?: 0.0
        }

        // CHF
        if (data.hasCHF) sigma += coeff[Covariants.HAS_CHF] ?: 0.0

        // CVD
        if (data.hasCVD) sigma += coeff[Covariants.HAS_CVD] ?: 0.0

        // CKD
        when (data.ckd) {
            CKD.G3 -> sigma += coeff[Covariants.HAS_CKD_G3] ?: 0.0
            CKD.G4 -> sigma += coeff[Covariants.HAS_CKD_G4] ?: 0.0
            CKD.G5 -> sigma += coeff[Covariants.HAS_CKD_G5] ?: 0.0
            CKD.G5D -> sigma += coeff[Covariants.HAS_CKD_G5D] ?: 0.0
            else -> {}
        }

        // GNRI Risk
        val risk = gnriRisk ?: return Double.NaN
        when (risk) {
            GNRIRisk.NO_RISK, GNRIRisk.LOW -> sigma += coeff[Covariants.GNRI_NO_OR_LOW] ?: 0.0
            GNRIRisk.MODERATE -> sigma += coeff[Covariants.GNRI_MODERATE] ?: 0.0
            GNRIRisk.MAJOR -> sigma += coeff[Covariants.GNRI_MAJOR] ?: 0.0
        }

        // Activity
        when (data.activity) {
            Activity.AMBULATORY -> sigma += coeff[Covariants.ACTIVITY_AMBULATORY] ?: 0.0
            Activity.WHEELCHAIR -> sigma += coeff[Covariants.ACTIVITY_WHEELCHAIR] ?: 0.0
            Activity.IMMOBILE -> sigma += coeff[Covariants.ACTIVITY_IMMOBILE] ?: 0.0
        }

        // Malignancy
        when (data.malignant) {
            MalignantNeoplasm.PAST_HISTORY -> sigma += coeff[Covariants.PAST_MALIGNANCY] ?: 0.0
            MalignantNeoplasm.UNDER_TREATMENT -> sigma += coeff[Covariants.TREATING_MALIGNANCY] ?: 0.0
            else -> {}
        }

        // Occlusive lesion
        if (!data.hasAILesion) sigma += coeff[Covariants.HAS_NO_AI_LESION] ?: 0.0
        if (!data.hasFPLesion) sigma += coeff[Covariants.HAS_NO_FP_LESION] ?: 0.0

        if (!data.hasAILesion) {
            if (data.hasFPLesion) {
                sigma += coeff[Covariants.LESION_FP] ?: 0.0
            } else if (data.hasBKLesion) {
                sigma += coeff[Covariants.LESION_BELOW_IP] ?: 0.0
            }
        }

        // Urgent
        if (data.isUrgent) sigma += coeff[Covariants.IS_URGENT] ?: 0.0

        // Fever
        if (data.hasFever) sigma += coeff[Covariants.FEVER] ?: 0.0

        // WBC
        if (data.hasAbnormalWBC) sigma += coeff[Covariants.ABNORMAL_WBC] ?: 0.0

        // Local Infection
        if (data.hasLocalInfection) sigma += coeff[Covariants.LOCAL_INFECTION] ?: 0.0

        // CAD
        if (data.hasCAD) sigma += coeff[Covariants.HAS_CAD] ?: 0.0

        // Smoking
        if (data.isSmoking) sigma += coeff[Covariants.IS_SMOKING] ?: 0.0

        // Dyslipidemia
        if (data.hasDyslipidemia) sigma += coeff[Covariants.HAS_DYSLIPIDEMIA] ?: 0.0

        // Contralateral
        if (!data.hasContraLateralLesion) sigma += coeff[Covariants.HAS_NO_CONTRALATERAL] ?: 0.0

        // Other VD
        if (data.hasOtherVD) sigma += coeff[Covariants.HAS_OTHER] ?: 0.0

        // Rutherford
        when (data.rutherford) {
            RutherfordClassification.CLASS4 -> sigma += coeff[Covariants.RUTHERFORD_4] ?: 0.0
            RutherfordClassification.CLASS5 -> sigma += coeff[Covariants.RUTHERFORD_5] ?: 0.0
            RutherfordClassification.CLASS6 -> sigma += coeff[Covariants.RUTHERFORD_6] ?: 0.0
        }

        sigma += coeff[Covariants.INTERCEPT] ?: 0.0

        return sigma
    }

    companion object {
        const val osH0Coeff = 0.922
        val osCoeff = mapOf(
            Covariants.IS_FEMALE to -0.25,
            Covariants.AGE_65_TO_74 to 0.31,
            Covariants.AGE_75_TO_84 to 0.76,
            Covariants.AGE_OVER_85 to 1.04,
            Covariants.HAS_CHF to 0.50,
            Covariants.HAS_CVD to 0.0,
            Covariants.HAS_CKD_G3 to 0.27,
            Covariants.HAS_CKD_G4 to 0.61,
            Covariants.HAS_CKD_G5 to 0.76,
            Covariants.HAS_CKD_G5D to 1.01,
            Covariants.GNRI_MODERATE to 0.14,
            Covariants.GNRI_MAJOR to 0.52,
            Covariants.ACTIVITY_WHEELCHAIR to 0.28,
            Covariants.ACTIVITY_IMMOBILE to 0.77,
            Covariants.PAST_MALIGNANCY to 0.20,
            Covariants.TREATING_MALIGNANCY to 0.56,
            Covariants.LESION_FP to -0.07,
            Covariants.LESION_BELOW_IP to 0.16
        )

        const val afsH0Coeff = 0.876
        val afsCoeff = mapOf(
            Covariants.IS_FEMALE to -0.21,
            Covariants.AGE_65_TO_74 to 0.19,
            Covariants.AGE_75_TO_84 to 0.42,
            Covariants.AGE_OVER_85 to 0.62,
            Covariants.HAS_CHF to 0.41,
            Covariants.HAS_CVD to 0.10,
            Covariants.HAS_CKD_G3 to 0.16,
            Covariants.HAS_CKD_G4 to 0.36,
            Covariants.HAS_CKD_G5 to 0.73,
            Covariants.HAS_CKD_G5D to 0.81,
            Covariants.GNRI_MODERATE to 0.09,
            Covariants.GNRI_MAJOR to 0.45,
            Covariants.ACTIVITY_WHEELCHAIR to 0.37,
            Covariants.ACTIVITY_IMMOBILE to 0.78,
            Covariants.PAST_MALIGNANCY to 0.15,
            Covariants.TREATING_MALIGNANCY to 0.39,
            Covariants.IS_URGENT to 0.34,
            Covariants.FEVER to 0.36,
            Covariants.ABNORMAL_WBC to 0.19,
            Covariants.LOCAL_INFECTION to 0.15,
            Covariants.LESION_FP to -0.07,
            Covariants.LESION_BELOW_IP to 0.15
        )

        val shortDeadOrAmputationCoeff = mapOf(
            Covariants.INTERCEPT to 2.86452,
            Covariants.ABNORMAL_WBC to -0.59896,
            Covariants.IS_URGENT to -0.64861,
            Covariants.HAS_CHF to -0.39326,
            Covariants.FEVER to -0.3888,
            Covariants.HAS_CKD_G5D to -0.33797,
            Covariants.HAS_NO_AI_LESION to -0.14474,
            Covariants.HAS_CVD to -0.05239,
            Covariants.HAS_DYSLIPIDEMIA to 0.05969,
            Covariants.RUTHERFORD_5 to 0.12638,
            Covariants.HAS_NO_FP_LESION to 0.17229,
            Covariants.GNRI_MODERATE to 0.36795,
            Covariants.ACTIVITY_AMBULATORY to 0.54391,
            Covariants.GNRI_NO_OR_LOW to 0.76479
        )

        val shortMALECoeff = mapOf(
            Covariants.INTERCEPT to 2.2575,
            Covariants.ABNORMAL_WBC to -0.50671,
            Covariants.FEVER to -0.33461,
            Covariants.LOCAL_INFECTION to -0.28088,
            Covariants.RUTHERFORD_6 to -0.26513,
            Covariants.ACTIVITY_WHEELCHAIR to -0.22555,
            Covariants.IS_URGENT to -0.20964,
            Covariants.HAS_CHF to -0.09218,
            Covariants.HAS_CKD_G5D to -0.02024,
            Covariants.HAS_CVD to 0.01592,
            Covariants.HAS_OTHER to 0.02649,
            Covariants.IS_SMOKING to 0.03109,
            Covariants.HAS_CAD to 0.0375,
            Covariants.RUTHERFORD_5 to 0.14299,
            Covariants.AGE_75_TO_84 to 0.16816,
            Covariants.ACTIVITY_AMBULATORY to 0.17103,
            Covariants.HAS_NO_CONTRALATERAL to 0.18822,
            Covariants.HAS_NO_FP_LESION to 0.21082,
            Covariants.HAS_DYSLIPIDEMIA to 0.2189,
            Covariants.IS_FEMALE to 0.24023,
            Covariants.GNRI_NO_OR_LOW to 0.32693,
            Covariants.AGE_OVER_85 to 0.46026,
            Covariants.GNRI_MODERATE to 0.46838
        )
    }
}
