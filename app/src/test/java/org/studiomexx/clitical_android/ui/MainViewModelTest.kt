package org.studiomexx.clitical_android.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    // Clearing wipes some twenty answers at once, so it can be undone (Material: prefer
    // undo over confirmation), and there's nothing to clear on an untouched form.

    @Test
    fun undoAfterClearingRestoresTheInput() {
        val viewModel = MainViewModel().apply {
            ageText = "65"
            albText = "4.0"
            updatePatientData { it.copy(isSmoking = true) }
        }

        viewModel.reset()
        assertEquals("", viewModel.ageText)
        viewModel.undoReset()

        assertEquals("65", viewModel.ageText)
        assertEquals("4.0", viewModel.albText)
        assertTrue(viewModel.patientData.isSmoking)
    }

    @Test
    fun canResetOnlyOnceSomethingWasEntered() {
        val viewModel = MainViewModel()
        assertFalse(viewModel.canReset)

        viewModel.heightText = "1"
        assertTrue(viewModel.canReset)

        viewModel.heightText = ""
        viewModel.updatePatientData { it.copy(hasCHF = true) }
        assertTrue(viewModel.canReset)

        viewModel.reset()
        assertFalse(viewModel.canReset)
    }

    @Test
    fun reassigningTheSameValueKeepsTheRisk() {
        val viewModel = predicted()

        viewModel.ageText = "65"
        viewModel.updatePatientData { it }

        assertNotNull(viewModel.calculatedRisk)
    }
}
