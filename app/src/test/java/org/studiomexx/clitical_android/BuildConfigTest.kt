package org.studiomexx.clitical_android

import org.junit.Assert.assertEquals
import org.junit.Test

class BuildConfigTest {
    @Test
    fun releaseVersionIs220Build7() {
        assertEquals("2.2.0", BuildConfig.VERSION_NAME)
        assertEquals(7, BuildConfig.VERSION_CODE)
    }
}
