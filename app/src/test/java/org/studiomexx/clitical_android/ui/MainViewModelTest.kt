package org.studiomexx.clitical_android.ui

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * On a wide window the result sits beside the form, so a shown risk must never
 * outlive the input it was computed from (as in the iOS app's regular width).
 */
class MainViewModelTest {

    private fun predicted() = MainViewModel().apply {
        ageText = "65"
        heightText = "150"
        weightText = "50"
        albText = "4.0"
        calculateRisk()
        assertNotNull(calculatedRisk)
    }

    @Test
    fun editingANumericFieldDiscardsTheRisk() {
        val viewModel = predicted()

        viewModel.ageText = "66"

        assertNull(viewModel.calculatedRisk)
    }

    @Test
    fun changingAnAnswerDiscardsTheRisk() {
        val viewModel = predicted()

        viewModel.updatePatientData { it.copy(isSmoking = !it.isSmoking) }

        assertNull(viewModel.calculatedRisk)
    }

    @Test
    fun reassigningTheSameValueKeepsTheRisk() {
        val viewModel = predicted()

        viewModel.ageText = "65"
        viewModel.updatePatientData { it }

        assertNotNull(viewModel.calculatedRisk)
    }
}
