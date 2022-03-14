package com.bunk.call.storage

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NumberOfCallStorageSharedPreferencesTest {

    private val sharedPreferences: SharedPreferences = mockk()
    private val sharedPreferencesProvider: SharedPreferencesProvider = mockk {
        every { getSharedPreferences(any()) } returns sharedPreferences
    }

    private val classUnderTest = NumberOfCallStorageSharedPreferences(sharedPreferencesProvider)

    @Test
    fun `getTimesQueried should return value`() {
        val phoneNumber = "+49 150 345 678"
        every { sharedPreferences.getInt(phoneNumber, 0) } returns 21

        val actual = classUnderTest.getTimesQueried(phoneNumber)

        assertThat(actual).isEqualTo(21)
    }

    @Test
    fun `incrementTimesQueried should not increment when phoneNumber is null`() {
        val editor: SharedPreferences.Editor = mockk()
        every { sharedPreferences.edit() } returns editor

        classUnderTest.incrementTimesQueried(null)

        verify(exactly = 0) { editor.putInt(any(), any()) }
    }

    @Test
    fun `incrementTimesQueried should increment when phoneNumber is not null`() {
        val editor: SharedPreferences.Editor = mockk(relaxed = true)
        every { sharedPreferences.edit() } returns editor
        every { sharedPreferences.getInt("+49 234 567", 0) } returns 2

        classUnderTest.incrementTimesQueried("+49 234 567")

        verify(exactly = 1) { editor.putInt("+49 234 567", 3) }
    }
}