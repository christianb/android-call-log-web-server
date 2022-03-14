package com.bunk.call.storage

import android.content.ContentResolver
import android.database.Cursor
import com.bunk.permission.AppPermission
import com.bunk.permission.PermissionHelper
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class CallLogStorageContentResolverTest {

    private val contentResolver: ContentResolver = mockk()
    private val permissionHelper: PermissionHelper = mockk()
    private val numberOfCallStorage: NumberOfCallStorageSharedPreferences = mockk()

    private val classUnderTest =
        CallLogStorageContentResolver(contentResolver, permissionHelper, numberOfCallStorage)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test(expected = IllegalArgumentException::class)
    fun `getCallLogs should throw Exception when permission not granted`() {
        every { permissionHelper.hasPermission(AppPermission.READ_CALL_LOG) } returns false

        classUnderTest.getCallLogs()
    }

    @Test
    fun `getCallLogs should return empty list when no contacts are stored`() {
        every { permissionHelper.hasPermission(AppPermission.READ_CALL_LOG) } returns true
        val cursor: Cursor = mockk(relaxed = true) {
            every { moveToNext() } returns false
        }
        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor

        val actual = classUnderTest.getCallLogs()

        assertThat(actual).isEmpty()
    }

    @Test
    fun `getCallLogs should return list and then cached list`() {
        every { permissionHelper.hasPermission(AppPermission.READ_CALL_LOG) } returns true
        val numberIndex = 1
        val nameIndex = 2
        val dateIndex = 3
        val durationIndex = 4

        val cursor: Cursor = mockk(relaxed = true) {
            every { moveToNext() } answers { true } andThen false
            every { getColumnIndex(android.provider.CallLog.Calls.NUMBER) } returns numberIndex
            every { getString(numberIndex) } returns "+49 170 123 456"
            every { getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME) } returns nameIndex
            every { getString(nameIndex) } returns "John Doe"
            every { getColumnIndex(android.provider.CallLog.Calls.DATE) } returns dateIndex
            every { getString(dateIndex) } returns "1647547679000"
            every { getColumnIndex(android.provider.CallLog.Calls.DURATION) } returns durationIndex
            every { getString(durationIndex) } returns "42"
        }

        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor
        every { numberOfCallStorage.getTimesQueried("+49 170 123 456") } returns 12

        val actual = classUnderTest.getCallLogs()
        assertThat(actual).isNotEmpty
        with(actual.first()) {
            assertThat(name).isEqualTo("John Doe")
            assertThat(number).isEqualTo("+49 170 123 456")
            assertThat(timesQueried).isEqualTo(12)
            assertThat(beginning).isEqualTo("2022-03-17T21:07:59+0100")
            assertThat(duration).isEqualTo("42")
        }

        val cached = classUnderTest.getCallLogs()
        assertThat(cached == actual).isTrue // check if the list is the same instance (cached)
    }

    @Test
    fun `should return list and new list when markedAsStale`() {
        every { permissionHelper.hasPermission(AppPermission.READ_CALL_LOG) } returns true
        val numberIndex = 1
        val nameIndex = 2
        val dateIndex = 3
        val durationIndex = 4

        val cursor: Cursor = mockk(relaxed = true) {
            every { moveToNext() } answers { true } andThen false
            every { getColumnIndex(android.provider.CallLog.Calls.NUMBER) } returns numberIndex
            every { getString(numberIndex) } returns "+49 170 123 456"
            every { getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME) } returns nameIndex
            every { getString(nameIndex) } returns "John Doe"
            every { getColumnIndex(android.provider.CallLog.Calls.DATE) } returns dateIndex
            every { getString(dateIndex) } returns "1647547679000"
            every { getColumnIndex(android.provider.CallLog.Calls.DURATION) } returns durationIndex
            every { getString(durationIndex) } returns "42"
        }

        every { contentResolver.query(any(), any(), any(), any(), any()) } returns cursor
        every { numberOfCallStorage.getTimesQueried("+49 170 123 456") } returns 12

        val actual = classUnderTest.getCallLogs()
        assertThat(actual).isNotEmpty
        with(actual.first()) {
            assertThat(name).isEqualTo("John Doe")
            assertThat(number).isEqualTo("+49 170 123 456")
            assertThat(timesQueried).isEqualTo(12)
            assertThat(beginning).isEqualTo("2022-03-17T21:07:59+0100")
            assertThat(duration).isEqualTo("42")
        }

        classUnderTest.markAsStale()

        val cached = classUnderTest.getCallLogs()
        assertThat(cached == actual).isFalse // check if the list is the same instance (cached)
    }
}