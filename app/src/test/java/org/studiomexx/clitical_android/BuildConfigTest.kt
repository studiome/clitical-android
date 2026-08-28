package org.studiomexx.clitical_android

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildConfigTest {
    @Test
    fun releaseVersionIs211Build6() {
        assertEquals("2.1.1", BuildConfig.VERSION_NAME)
        assertEquals(6, BuildConfig.VERSION_CODE)
    }
}
