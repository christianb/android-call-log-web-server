package com.bunk.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class PermissionHelperTest {

    private val applicationContext: Context = mockk(relaxed = true)

    private val classUnderTest = PermissionHelper(applicationContext)

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `hasPermission should return true when permission granted`() {
        every {
            ContextCompat.checkSelfPermission(
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_GRANTED

        val actual = classUnderTest.hasPermission(AppPermission.READ_PHONE_STATE)

        assertThat(actual).isTrue
    }

    @Test
    fun `hasPermission should return false when permission not granted`() {
        every {
            ContextCompat.checkSelfPermission(
                any(),
                any()
            )
        } returns PackageManager.PERMISSION_DENIED

        val actual = classUnderTest.hasPermission(AppPermission.READ_PHONE_STATE)

        assertThat(actual).isFalse
    }
}