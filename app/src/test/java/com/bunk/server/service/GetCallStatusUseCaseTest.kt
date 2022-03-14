package com.bunk.server.service

import com.bunk.call.OngoingCall
import com.bunk.call.storage.CallLogStorage
import com.bunk.call.storage.NumberOfCallStorage
import com.bunk.call.storage.OngoingCallStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetCallStatusUseCaseTest {

    private val ongoingCallStorage: OngoingCallStorage = mockk()
    private val numberOfCallStorage: NumberOfCallStorage = mockk(relaxed = true)
    private val callLogStorage: CallLogStorage = mockk(relaxed = true)

    private val classUnderTest =
        GetCallStatusUseCase(ongoingCallStorage, numberOfCallStorage, callLogStorage)

    @Test
    fun `execute should return OngoingCall`() {
        val expected = OngoingCall(ongoing = true, number = "123", name = "John")
        every { ongoingCallStorage.getOngoingCall() } returns expected

        val actual = classUnderTest.execute()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `execute should increment times queried when ongoing is true`() {
        val expected = OngoingCall(ongoing = true, number = "+49 234 567", name = "any-name")
        every { ongoingCallStorage.getOngoingCall() } returns expected

        classUnderTest.execute()

        verify { numberOfCallStorage.incrementTimesQueried("+49 234 567") }
    }

    @Test
    fun `execute should not increment times queried when ongoing is false`() {
        val expected = OngoingCall(ongoing = false, number = "+49 234 567", name = "any-name")
        every { ongoingCallStorage.getOngoingCall() } returns expected

        classUnderTest.execute()

        verify(exactly = 0) { numberOfCallStorage.incrementTimesQueried(any()) }
    }

    @Test
    fun `execute should mark callLog as stale when ongoing is true`() {
        val expected = OngoingCall(ongoing = true, number = "any-number", name = "any-name")
        every { ongoingCallStorage.getOngoingCall() } returns expected

        classUnderTest.execute()

        verify { callLogStorage.markAsStale() }
    }

    @Test
    fun `execute should not mark callLog as stale when ongoing is false`() {
        val expected = OngoingCall(ongoing = false, number = "any-number", name = "any-name")
        every { ongoingCallStorage.getOngoingCall() } returns expected

        classUnderTest.execute()

        verify(exactly = 0) { callLogStorage.markAsStale() }
    }
}