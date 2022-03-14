package com.bunk.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.bunk.R
import com.bunk.call.CallLog
import com.bunk.server.Server
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module

class MainActivityTest {

    @get:Rule
    val composeTestRule: ComposeTestRule = createEmptyComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE
    )

    private val viewModel: MainViewModel = mockk(relaxed = true)

    @get:Rule
    val koinTestRule = KoinTestRule(
        module {
            single { viewModel }
        }
    )

    @Test
    fun shouldDisplayServerInfo() {
        val serverInfo =
            Server.Info(ip = "192.168.188.5", port = "8080", startTime = "some-startTime")
        every { viewModel.serverInfoLiveData } returns MutableLiveData(serverInfo)

        ActivityScenario.launch<MainActivity>(INTENT)

        composeTestRule.onNodeWithTag("serverStatus-Ip")
            .assertIsDisplayed()
            .assertTextEquals(String.format(getString(R.string.ip_address), "192.168.188.5"))

        composeTestRule.onNodeWithTag("serverStatus-Port")
            .assertIsDisplayed()
            .assertTextEquals(String.format(getString(R.string.port), "8080"))
    }

    @Test
    fun shouldDisplayCallLogs() {
        val callLog = CallLog(
            beginning = "",
            duration = "13",
            number = "",
            name = "John Doe",
            timesQueried = 42
        )
        every { viewModel.callLogLiveData } returns MutableLiveData(listOf(callLog))

        ActivityScenario.launch<MainActivity>(INTENT)

        composeTestRule.onNodeWithTag("callListView").assertIsDisplayed()

        composeTestRule.onNodeWithTag("listItem-Name")
            .assertIsDisplayed()
            .assertTextEquals(String.format(getString(R.string.call_log_name), "John Doe"))

        composeTestRule.onNodeWithTag("listItem-Duration")
            .assertIsDisplayed()
            .assertTextEquals(String.format(getString(R.string.caller_log_duration), "13"))
    }

    private fun getString(@StringRes resId: Int): String {
        return ApplicationProvider.getApplicationContext<Context?>().resources.getString(resId)
    }

    companion object {
        val INTENT = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
    }
}