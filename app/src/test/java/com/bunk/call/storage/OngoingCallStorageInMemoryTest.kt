package com.bunk.call.storage

import com.bunk.call.OngoingCall
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class OngoingCallStorageInMemoryTest {

    private val classUnderTest = OngoingCallStorageInMemory()

    @Test
    fun saveOngoingCall() {
        val expected = OngoingCall(ongoing = true, number = "+49 456 89", name = "John Doe")
        classUnderTest.saveOngoingCall(expected)

        val actual = classUnderTest.getOngoingCall()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `getOngoingCall should return default when not yet set`() {
        val actual = classUnderTest.getOngoingCall()

        assertThat(actual).matches { !it.ongoing && it.number == null && it.name == null }
    }
}