package com.bunk.server

import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.bunk.server.service.ServiceFactory
import com.bunk.util.DateTimeUtil
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class KtorServerTest {

    private val wifiManager: WifiManager = mockk(relaxed = true)
    private val serviceFactory: ServiceFactory = mockk()

    private val classUnderTest = KtorServer(wifiManager, serviceFactory)

    @Before
    fun setUp() {
        mockkStatic(Formatter::class)
        mockkObject(DateTimeUtil)

        every { serviceFactory.createRootService(any(), any()) } returns mockk(relaxed = true)
        every { serviceFactory.createLogService() } returns mockk(relaxed = true)
        every { serviceFactory.createStatusService() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
        classUnderTest.stopServer()
    }

    @Test
    fun `getServerInfo should return ServerInfo with IP`() {
        every { wifiManager.connectionInfo.ipAddress } returns 42
        every { Formatter.formatIpAddress(42) } returns "192.168.188.1"

        val actual = classUnderTest.getServerInfo()

        assertThat(actual.ip).isEqualTo("192.168.188.1")
    }

    @Test
    fun `getServerInfo should return ServerInfo with empty startTime when server not yet started`() {
        every { Formatter.formatIpAddress(any()) } returns ""

        val actual = classUnderTest.getServerInfo()

        assertThat(actual.startTime).isEqualTo("")
    }

    @Test
    fun `getServerInfo should return ServerInfo with startTime when server was started`() {
        every { Formatter.formatIpAddress(any()) } returns ""
        every { DateTimeUtil.format(any(), any()) } returns "2022-03-16T18:40:56+0100"
        classUnderTest.startServer()

        val actual = classUnderTest.getServerInfo()

        assertThat(actual.startTime).isEqualTo("2022-03-16T18:40:56+0100")
    }

    @Test
    fun `startServer should not start twice`() {
        every { Formatter.formatIpAddress(any()) } returns ""
        every { DateTimeUtil.format(any(), any()) } returns "2022-03-16T18:40:56+0100"
        classUnderTest.startServer()

        val first = classUnderTest.getServerInfo()
        assertThat(first.startTime).isEqualTo("2022-03-16T18:40:56+0100")
        every { DateTimeUtil.format(any(), any()) } returns "2023-04-16T18:40:56+0100"
        classUnderTest.startServer()

        val second = classUnderTest.getServerInfo()
        assertThat(second.startTime).isEqualTo("2022-03-16T18:40:56+0100")
    }
}