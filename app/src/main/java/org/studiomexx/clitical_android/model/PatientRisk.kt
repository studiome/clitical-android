package org.studiomexx.clitical_android.model

import java.util.EnumSet
import kotlin.math.exp
import kotlin.math.pow

class PatientRisk(patientData: PatientData) {

    val gnri: Double
    val gnriRisk: GNRIRisk?
    val predictedOS: Double
    val predictedAFS: Double
    val osRisk: OSRisk?
    val predicted30DDeathOrAmputation: Double
    val predicted30DayMALE: Double

    init {
        // Callers validate first; this guards the model against being fed unchecked data.
        patientData.validate()?.let { throw FormatException("invalid patient data: $it", it) }

        gnri = calculateGNRI(patientData)
        gnriRisk = classifyGNRIRisk(gnri)

        if (gnriRisk == null) {
            predictedOS = Double.NaN
            predictedAFS = Double.NaN
            osRisk = null
            predicted30DDeathOrAmputation = Double.NaN
            predicted30DayMALE = Double.NaN
        } else {
            val covariates = extractCovariates(patientData, gnriRisk)

            val osSigma = calculateSigma(osCoefficients, covariates)
            predictedOS = OS_H0_COEFFICIENT.pow(exp(osSigma))
            osRisk = classifyOSRisk(predictedOS)

            val afsSigma = calculateSigma(afsCoefficients, covariates)
            predictedAFS = AFS_H0_COEFFICIENT.pow(exp(afsSigma))

            val dorASigma = calculateSigma(shortDeadOrAmputationCoefficients, covariates)
            predicted30DDeathOrAmputation = sigmoid(dorASigma)

            val maleSigma = calculateSigma(shortMALECoefficients, covariates)
            predicted30DayMALE = sigmoid(maleSigma)
        }
    }

    private fun calculateGNRI(data: PatientData): Double {
        val height = data.height ?: return Double.NaN
        val weight = data.weight ?: return Double.NaN
        val alb = data.alb ?: return Double.NaN
        val wi = (weight / (22.0 * height * height)).coerceAtMost(1.0)
        return (14.89 * alb) + (41.7 * wi)
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

    private fun classifyOSRisk(overallSurvival: Double): OSRisk? {
        if (overallSurvival.isNaN()) return null
        return when {
            overallSurvival >= 0.70 -> OSRisk.LOW
            overallSurvival >= 0.50 -> OSRisk.MEDIUM
            else -> OSRisk.HIGH
        }
    }

    private fun sigmoid(sigma: Double): Double {
        if (sigma.isNaN()) return Double.NaN
        return 1.0 / (1.0 + exp(sigma))
    }

    // Covariates mapping helper
    private enum class Covariates {
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
        INTERCEPT,
    }

    private fun extractCovariates(data: PatientData, gnriRisk: GNRIRisk): Set<Covariates> {
        val covariates = EnumSet.noneOf(Covariates::class.java)
        // validate() rejects a missing age; failing loudly beats scoring a partial covariate set.
        val age = checkNotNull(data.age) { "age is required" }

        if (data.sex == Sex.FEMALE) covariates.add(Covariates.IS_FEMALE)

        when {
            age >= 85 -> covariates.add(Covariates.AGE_OVER_85)
            age >= 75 -> covariates.add(Covariates.AGE_75_TO_84)
            age >= 65 -> covariates.add(Covariates.AGE_65_TO_74)
        }

        if (data.hasCHF) covariates.add(Covariates.HAS_CHF)
        if (data.hasCVD) covariates.add(Covariates.HAS_CVD)

        when (data.ckd) {
            CKD.G3 -> covariates.add(Covariates.HAS_CKD_G3)
            CKD.G4 -> covariates.add(Covariates.HAS_CKD_G4)
            CKD.G5 -> covariates.add(Covariates.HAS_CKD_G5)
            CKD.G5D -> covariates.add(Covariates.HAS_CKD_G5D)
            else -> {}
        }

        when (gnriRisk) {
            GNRIRisk.NO_RISK, GNRIRisk.LOW -> covariates.add(Covariates.GNRI_NO_OR_LOW)
            GNRIRisk.MODERATE -> covariates.add(Covariates.GNRI_MODERATE)
            GNRIRisk.MAJOR -> covariates.add(Covariates.GNRI_MAJOR)
        }

        when (data.activity) {
            Activity.AMBULATORY -> covariates.add(Covariates.ACTIVITY_AMBULATORY)
            Activity.WHEELCHAIR -> covariates.add(Covariates.ACTIVITY_WHEELCHAIR)
            Activity.IMMOBILE -> covariates.add(Covariates.ACTIVITY_IMMOBILE)
        }

        when (data.malignant) {
            MalignantNeoplasm.PAST_HISTORY -> covariates.add(Covariates.PAST_MALIGNANCY)
            MalignantNeoplasm.UNDER_TREATMENT -> covariates.add(Covariates.TREATING_MALIGNANCY)
            else -> {}
        }

        if (!data.hasAILesion) covariates.add(Covariates.HAS_NO_AI_LESION)
        if (!data.hasFPLesion) covariates.add(Covariates.HAS_NO_FP_LESION)

        if (!data.hasAILesion) {
            if (data.hasFPLesion) {
                covariates.add(Covariates.LESION_FP)
            } else if (data.hasBKLesion) {
                covariates.add(Covariates.LESION_BELOW_IP)
            }
        }

        if (data.isUrgent) covariates.add(Covariates.IS_URGENT)
        if (data.hasFever) covariates.add(Covariates.FEVER)
        if (data.hasAbnormalWBC) covariates.add(Covariates.ABNORMAL_WBC)
        if (data.hasLocalInfection) covariates.add(Covariates.LOCAL_INFECTION)
        if (data.hasCAD) covariates.add(Covariates.HAS_CAD)
        if (data.isSmoking) covariates.add(Covariates.IS_SMOKING)
        if (data.hasDyslipidemia) covariates.add(Covariates.HAS_DYSLIPIDEMIA)
        if (!data.hasContralateralLesion) covariates.add(Covariates.HAS_NO_CONTRALATERAL)
        if (data.hasOtherVD) covariates.add(Covariates.HAS_OTHER)

        when (data.rutherford) {
            RutherfordClassification.CLASS4 -> covariates.add(Covariates.RUTHERFORD_4)
            RutherfordClassification.CLASS5 -> covariates.add(Covariates.RUTHERFORD_5)
            RutherfordClassification.CLASS6 -> covariates.add(Covariates.RUTHERFORD_6)
        }

        return covariates
    }

    private fun calculateSigma(coefficients: Map<Covariates, Double>, patientCovariates: Set<Covariates>): Double {
        var sigma = coefficients[Covariates.INTERCEPT] ?: 0.0
        for ((covariate, weight) in coefficients) {
            if (covariate != Covariates.INTERCEPT && covariate in patientCovariates) {
                sigma += weight
            }
        }
        return sigma
    }

    private companion object {
        const val OS_H0_COEFFICIENT = 0.922
        val osCoefficients = mapOf(
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
            Covariates.LESION_BELOW_IP to 0.16,
        )

        const val AFS_H0_COEFFICIENT = 0.876
        val afsCoefficients = mapOf(
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
            Covariates.LESION_BELOW_IP to 0.15,
        )

        val shortDeadOrAmputationCoefficients = mapOf(
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
            Covariates.GNRI_NO_OR_LOW to 0.76479,
        )

        val shortMALECoefficients = mapOf(
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
            Covariates.GNRI_MODERATE to 0.46838,
        )
    }
}
