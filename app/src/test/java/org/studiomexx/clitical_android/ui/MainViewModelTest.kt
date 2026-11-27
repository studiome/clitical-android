package org.studiomexx.clitical_android.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.studiomexx.clitical_android.model.ValidationError

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

    // The snackbar outlives the user's next keystroke; Undo must not then overwrite it.
    @Test
    fun undoAfterTypingFollowingAClearIsANoOp() {
        val viewModel = MainViewModel().apply { ageText = "65" }

        viewModel.reset()
        viewModel.heightText = "170"
        viewModel.undoReset()

        assertEquals("", viewModel.ageText)
        assertEquals("170", viewModel.heightText)
    }

    @Test
    fun undoAfterChangingAnAnswerFollowingAClearIsANoOp() {
        val viewModel = MainViewModel().apply { ageText = "65" }

        viewModel.reset()
        viewModel.updatePatientData { it.copy(isSmoking = true) }
        viewModel.undoReset()

        assertEquals("", viewModel.ageText)
    }

    // Pressing Predict flips hasSubmitted, which is UI state rather than an answer, so it
    // must not make the pending Undo stale.
    @Test
    fun undoAfterPredictingFollowingAClearStillRestoresTheInput() {
        val viewModel = MainViewModel().apply { ageText = "65" }

        viewModel.reset()
        viewModel.calculateRisk()
        viewModel.undoReset()

        assertEquals("65", viewModel.ageText)
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
    fun heightInMetresTypedAsCentimetresIsOutOfRange() {
        val viewModel = MainViewModel().apply {
            ageText = "65"
            heightText = "1.6"
            weightText = "50"
            albText = "4.0"
        }

        assertEquals(ValidationError.OUT_OF_RANGE, viewModel.calculateRisk())
        assertNull(viewModel.calculatedRisk)
        assertEquals(ValidationError.OUT_OF_RANGE, viewModel.validationError)
    }

    @Test
    fun outOfRangeIsFlaggedBeforeAnySubmit() {
        val viewModel = MainViewModel()

        viewModel.heightText = "1.6"
        viewModel.ageText = "999"
        viewModel.weightText = "0"
        viewModel.albText = "99.9"

        assertEquals(FieldIssue.OUT_OF_RANGE, viewModel.heightIssue)
        assertEquals(FieldIssue.OUT_OF_RANGE, viewModel.ageIssue)
        assertEquals(FieldIssue.OUT_OF_RANGE, viewModel.weightIssue)
        assertEquals(FieldIssue.OUT_OF_RANGE, viewModel.albIssue)
    }

    @Test
    fun inRangeBoundsHaveNoIssue() {
        val viewModel = MainViewModel().apply {
            ageText = "18"
            heightText = "250"
            weightText = "20"
            albText = "6.0"
        }

        assertNull(viewModel.ageIssue)
        assertNull(viewModel.heightIssue)
        assertNull(viewModel.weightIssue)
        assertNull(viewModel.albIssue)
    }

    @Test
    fun emptyFieldIsMissingOnlyAfterASubmit() {
        val viewModel = MainViewModel()
        assertNull(viewModel.ageIssue)

        viewModel.calculateRisk()

        assertEquals(FieldIssue.MISSING, viewModel.ageIssue)
    }

    @Test
    fun reassigningTheSameValueKeepsTheRisk() {
        val viewModel = predicted()

        viewModel.ageText = "65"
        viewModel.updatePatientData { it }

        assertNotNull(viewModel.calculatedRisk)
    }
}
