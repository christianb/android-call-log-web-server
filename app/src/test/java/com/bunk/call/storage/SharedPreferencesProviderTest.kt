package com.bunk.call.storage

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SharedPreferencesProviderTest {
    private val applicationContext: Context = mockk()

    private val classUnderTesT = SharedPreferencesProvider(applicationContext)

    @Test
    fun getSharedPreferences() {
        val expected: SharedPreferences = mockk()
        every {
            applicationContext.getSharedPreferences(
                "some-name",
                Context.MODE_PRIVATE
            )
        } returns expected

        val actual = classUnderTesT.getSharedPreferences("some-name")

        assertThat(actual).isEqualTo(expected)
    }
}