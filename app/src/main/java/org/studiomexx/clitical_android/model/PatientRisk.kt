package org.studiomexx.clitical_android.model

import kotlin.math.exp
import kotlin.math.pow

class PatientRisk(patientData: PatientData) {

    init {
        // Callers validate first; this guards the model against being fed unchecked data.
        patientData.validate()?.let { throw FormatException("invalid patient data: $it", it) }
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
        var wi = weight / (22.0 * height.pow(2.0))
        if (wi >= 1.0) wi = 1.0
        return 14.89 * alb + 41.7 * wi
    }

    // Derived straight from the data (not from the gnriRisk property) so the sigma calculations
    // below do not depend on property declaration order.
    private fun gnriRiskOf(data: PatientData): GNRIRisk? = classifyGNRIRisk(calcGNRI(data))

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
        val sigma = calcSigma(data, osCoeff, gnriRiskOf(data))
        if (sigma.isNaN()) return Double.NaN
        return OS_H0_COEFF.pow(exp(sigma))
    }

    private fun calcPredictedAFS(data: PatientData): Double {
        val sigma = calcSigma(data, afsCoeff, gnriRiskOf(data))
        if (sigma.isNaN()) return Double.NaN
        return AFS_H0_COEFF.pow(exp(sigma))
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
        val sigma = calcSigma(data, shortDeadOrAmputationCoeff, gnriRiskOf(data))
        if (sigma.isNaN()) return Double.NaN
        return 1.0 / (1.0 + exp(sigma))
    }

    private fun calc30DMALE(data: PatientData): Double {
        val sigma = calcSigma(data, shortMALECoeff, gnriRiskOf(data))
        if (sigma.isNaN()) return Double.NaN
        return 1.0 / (1.0 + exp(sigma))
    }

    // Covariates mapping helper
    enum class Covariates {
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

    private fun calcSigma(data: PatientData, coeff: Map<Covariates, Double>, gnriRisk: GNRIRisk?): Double {
        var sigma = 0.0
        val age = data.age ?: return Double.NaN

        // sex
        if (data.sex == Sex.FEMALE) sigma += coeff[Covariates.IS_FEMALE] ?: 0.0

        // age
        when {
            age >= 85 -> sigma += coeff[Covariates.AGE_OVER_85] ?: 0.0
            age >= 75 -> sigma += coeff[Covariates.AGE_75_TO_84] ?: 0.0
            age >= 65 -> sigma += coeff[Covariates.AGE_65_TO_74] ?: 0.0
        }

        // CHF
        if (data.hasCHF) sigma += coeff[Covariates.HAS_CHF] ?: 0.0

        // CVD
        if (data.hasCVD) sigma += coeff[Covariates.HAS_CVD] ?: 0.0

        // CKD
        sigma += when (data.ckd) {
            CKD.G3 -> coeff[Covariates.HAS_CKD_G3] ?: 0.0
            CKD.G4 -> coeff[Covariates.HAS_CKD_G4] ?: 0.0
            CKD.G5 -> coeff[Covariates.HAS_CKD_G5] ?: 0.0
            CKD.G5D -> coeff[Covariates.HAS_CKD_G5D] ?: 0.0
            else -> 0.0
        }

        // GNRI Risk
        val risk = gnriRisk ?: return Double.NaN
        sigma += when (risk) {
            GNRIRisk.NO_RISK, GNRIRisk.LOW -> coeff[Covariates.GNRI_NO_OR_LOW] ?: 0.0
            GNRIRisk.MODERATE -> coeff[Covariates.GNRI_MODERATE] ?: 0.0
            GNRIRisk.MAJOR -> coeff[Covariates.GNRI_MAJOR] ?: 0.0
        }

        // Activity
        sigma += when (data.activity) {
            Activity.AMBULATORY -> coeff[Covariates.ACTIVITY_AMBULATORY] ?: 0.0
            Activity.WHEELCHAIR -> coeff[Covariates.ACTIVITY_WHEELCHAIR] ?: 0.0
            Activity.IMMOBILE -> coeff[Covariates.ACTIVITY_IMMOBILE] ?: 0.0
        }

        // Malignancy
        sigma += when (data.malignant) {
            MalignantNeoplasm.PAST_HISTORY -> coeff[Covariates.PAST_MALIGNANCY] ?: 0.0
            MalignantNeoplasm.UNDER_TREATMENT -> coeff[Covariates.TREATING_MALIGNANCY] ?: 0.0
            else -> 0.0
        }

        // Occlusive lesion
        if (!data.hasAILesion) sigma += coeff[Covariates.HAS_NO_AI_LESION] ?: 0.0
        if (!data.hasFPLesion) sigma += coeff[Covariates.HAS_NO_FP_LESION] ?: 0.0

        if (!data.hasAILesion) {
            if (data.hasFPLesion) {
                sigma += coeff[Covariates.LESION_FP] ?: 0.0
            } else if (data.hasBKLesion) {
                sigma += coeff[Covariates.LESION_BELOW_IP] ?: 0.0
            }
        }

        // Urgent
        if (data.isUrgent) sigma += coeff[Covariates.IS_URGENT] ?: 0.0

        // Fever
        if (data.hasFever) sigma += coeff[Covariates.FEVER] ?: 0.0

        // WBC
        if (data.hasAbnormalWBC) sigma += coeff[Covariates.ABNORMAL_WBC] ?: 0.0

        // Local Infection
        if (data.hasLocalInfection) sigma += coeff[Covariates.LOCAL_INFECTION] ?: 0.0

        // CAD
        if (data.hasCAD) sigma += coeff[Covariates.HAS_CAD] ?: 0.0

        // Smoking
        if (data.isSmoking) sigma += coeff[Covariates.IS_SMOKING] ?: 0.0

        // Dyslipidemia
        if (data.hasDyslipidemia) sigma += coeff[Covariates.HAS_DYSLIPIDEMIA] ?: 0.0

        // Contralateral
        if (!data.hasContralateralLesion) sigma += coeff[Covariates.HAS_NO_CONTRALATERAL] ?: 0.0

        // Other VD
        if (data.hasOtherVD) sigma += coeff[Covariates.HAS_OTHER] ?: 0.0

        // Rutherford
        sigma += when (data.rutherford) {
            RutherfordClassification.CLASS4 -> coeff[Covariates.RUTHERFORD_4] ?: 0.0
            RutherfordClassification.CLASS5 -> coeff[Covariates.RUTHERFORD_5] ?: 0.0
            RutherfordClassification.CLASS6 -> coeff[Covariates.RUTHERFORD_6] ?: 0.0
        }

        sigma += coeff[Covariates.INTERCEPT] ?: 0.0

        return sigma
    }

    companion object {
        const val OS_H0_COEFF = 0.922
        val osCoeff = mapOf(
            Covariates.IS_FEMALE to -0.25,
            Covariates.AGE_65_TO_74 to 0.31,
            Covariates.AGE_75_TO_84 to 0.76,
            Covariates.AGE_OVER_85 to 1.04,
            Covariates.HAS_CHF to 0.50,
            Covariates.HAS_CVD to 0.0,
            Covariates.HAS_CKD_G3 to 0.27,
            Covariates.HAS_CKD_G4 to 0.61,
            Covariates.HAS_CKD_G5 to 0.76,
            Covariates.HAS_CKD_G5D to 1.01,
            Covariates.GNRI_MODERATE to 0.14,
            Covariates.GNRI_MAJOR to 0.52,
            Covariates.ACTIVITY_WHEELCHAIR to 0.28,
            Covariates.ACTIVITY_IMMOBILE to 0.77,
            Covariates.PAST_MALIGNANCY to 0.20,
            Covariates.TREATING_MALIGNANCY to 0.56,
            Covariates.LESION_FP to -0.07,
            Covariates.LESION_BELOW_IP to 0.16
        )

        const val AFS_H0_COEFF = 0.876
        val afsCoeff = mapOf(
            Covariates.IS_FEMALE to -0.21,
            Covariates.AGE_65_TO_74 to 0.19,
            Covariates.AGE_75_TO_84 to 0.42,
            Covariates.AGE_OVER_85 to 0.62,
            Covariates.HAS_CHF to 0.41,
            Covariates.HAS_CVD to 0.10,
            Covariates.HAS_CKD_G3 to 0.16,
            Covariates.HAS_CKD_G4 to 0.36,
            Covariates.HAS_CKD_G5 to 0.73,
            Covariates.HAS_CKD_G5D to 0.81,
            Covariates.GNRI_MODERATE to 0.09,
            Covariates.GNRI_MAJOR to 0.45,
            Covariates.ACTIVITY_WHEELCHAIR to 0.37,
            Covariates.ACTIVITY_IMMOBILE to 0.78,
            Covariates.PAST_MALIGNANCY to 0.15,
            Covariates.TREATING_MALIGNANCY to 0.39,
            Covariates.IS_URGENT to 0.34,
            Covariates.FEVER to 0.36,
            Covariates.ABNORMAL_WBC to 0.19,
            Covariates.LOCAL_INFECTION to 0.15,
            Covariates.LESION_FP to -0.07,
            Covariates.LESION_BELOW_IP to 0.15
        )

        val shortDeadOrAmputationCoeff = mapOf(
            Covariates.INTERCEPT to 2.86452,
            Covariates.ABNORMAL_WBC to -0.59896,
            Covariates.IS_URGENT to -0.64861,
            Covariates.HAS_CHF to -0.39326,
            Covariates.FEVER to -0.3888,
            Covariates.HAS_CKD_G5D to -0.33797,
            Covariates.HAS_NO_AI_LESION to -0.14474,
            Covariates.HAS_CVD to -0.05239,
            Covariates.HAS_DYSLIPIDEMIA to 0.05969,
            Covariates.RUTHERFORD_5 to 0.12638,
            Covariates.HAS_NO_FP_LESION to 0.17229,
            Covariates.GNRI_MODERATE to 0.36795,
            Covariates.ACTIVITY_AMBULATORY to 0.54391,
            Covariates.GNRI_NO_OR_LOW to 0.76479
        )

        val shortMALECoeff = mapOf(
            Covariates.INTERCEPT to 2.2575,
            Covariates.ABNORMAL_WBC to -0.50671,
            Covariates.FEVER to -0.33461,
            Covariates.LOCAL_INFECTION to -0.28088,
            Covariates.RUTHERFORD_6 to -0.26513,
            Covariates.ACTIVITY_WHEELCHAIR to -0.22555,
            Covariates.IS_URGENT to -0.20964,
            Covariates.HAS_CHF to -0.09218,
            Covariates.HAS_CKD_G5D to -0.02024,
            Covariates.HAS_CVD to 0.01592,
            Covariates.HAS_OTHER to 0.02649,
            Covariates.IS_SMOKING to 0.03109,
            Covariates.HAS_CAD to 0.0375,
            Covariates.RUTHERFORD_5 to 0.14299,
            Covariates.AGE_75_TO_84 to 0.16816,
            Covariates.ACTIVITY_AMBULATORY to 0.17103,
            Covariates.HAS_NO_CONTRALATERAL to 0.18822,
            Covariates.HAS_NO_FP_LESION to 0.21082,
            Covariates.HAS_DYSLIPIDEMIA to 0.2189,
            Covariates.IS_FEMALE to 0.24023,
            Covariates.GNRI_NO_OR_LOW to 0.32693,
            Covariates.AGE_OVER_85 to 0.46026,
            Covariates.GNRI_MODERATE to 0.46838
        )
    }
}
