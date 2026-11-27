package org.studiomexx.clitical_android.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PatientDataTest {

    private fun valid() = PatientData(age = 65, height = 1.5, weight = 50.0, alb = 4.0)

    @Test
    fun validDataHasNoError() {
        assertNull(valid().validate())
    }

    @Test
    fun missingNumbersAreEmptyFields() {
        assertEquals(ValidationError.EMPTY_FIELDS, PatientData().validate())
        assertEquals(ValidationError.EMPTY_FIELDS, valid().copy(age = null).validate())
        assertEquals(ValidationError.EMPTY_FIELDS, valid().copy(height = null).validate())
        assertEquals(ValidationError.EMPTY_FIELDS, valid().copy(weight = null).validate())
        assertEquals(ValidationError.EMPTY_FIELDS, valid().copy(alb = null).validate())
    }

    @Test
    fun noLesionIsReported() {
        val data = valid().copy(hasAILesion = false, hasFPLesion = false, hasBKLesion = false)
        assertEquals(ValidationError.NO_LESION_SELECTED, data.validate())
    }

    @Test
    fun boundsAreInclusive() {
        assertNull(valid().copy(age = 18).validate())
        assertNull(valid().copy(age = 120).validate())
        assertNull(valid().copy(height = 1.0).validate())
        assertNull(valid().copy(height = 2.5).validate())
        assertNull(valid().copy(weight = 20.0).validate())
        assertNull(valid().copy(weight = 300.0).validate())
        assertNull(valid().copy(alb = 1.0).validate())
        assertNull(valid().copy(alb = 6.0).validate())
    }

    @Test
    fun justOutsideTheBoundsIsOutOfRange() {
        val outOfRange = ValidationError.OUT_OF_RANGE
        assertEquals(outOfRange, valid().copy(age = 17).validate())
        assertEquals(outOfRange, valid().copy(age = 121).validate())
        assertEquals(outOfRange, valid().copy(height = 0.99).validate())
        assertEquals(outOfRange, valid().copy(height = 2.51).validate())
        assertEquals(outOfRange, valid().copy(weight = 19.9).validate())
        assertEquals(outOfRange, valid().copy(weight = 300.1).validate())
        assertEquals(outOfRange, valid().copy(alb = 0.9).validate())
        assertEquals(outOfRange, valid().copy(alb = 6.1).validate())
    }
}
