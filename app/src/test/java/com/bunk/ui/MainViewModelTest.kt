package com.bunk.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.bunk.call.CallLog
import com.bunk.call.storage.CallLogStorage
import com.bunk.server.Server
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val server: Server = mockk()
    private val callLogStorage: CallLogStorage = mockk()

    @Test
    fun `getServerInfoLiveData should have ServerInfo`() {
        val expected = Server.Info(ip = "some-ip", port = "some-port", startTime = "some-startTime")

        classUnderTest(serverInfo = expected).serverInfoLiveData.test().assertValue(expected)
    }

    @Test
    fun `getCallLogLiveData should have no value by default`() {
        classUnderTest().callLogLiveData.test().assertNoValue()
    }

    @Test
    fun `refresh should set value to CallLogLiveData`() {
        val callLog = CallLog(
            beginning = "some-beginning",
            duration = "some-duration",
            number = "some-number",
            name = "some-name",
            timesQueried = 42
        )
        val expected: List<CallLog> = listOf(callLog)
        every { callLogStorage.getCallLogs() } returns expected
        val classUnderTest = classUnderTest()
        classUnderTest.refresh()

        classUnderTest.callLogLiveData.test().assertValue(expected)
    }

    private fun classUnderTest(serverInfo: Server.Info? = null): MainViewModel {
        every { server.getServerInfo() } returns (serverInfo ?: Server.Info(
            ip = "default-ip",
            port = "default-port",
            startTime = "default-startTime"
        ))
        return MainViewModel(server, callLogStorage)
    }
}