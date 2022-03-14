package com.bunk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bunk.R
import com.bunk.call.CallLog
import com.bunk.server.Server

@Composable
fun Screen(
    serverInfo: Server.Info,
    callLogList: List<CallLog>
) {
    Column {
        ServerStatus(serverInfo)
        CallListView(callLogList)
    }
}

@Composable
private fun ServerStatus(serverInfo: Server.Info) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
    ) {
        val padding = 6.dp
        Text(
            text = String.format(stringResource(id = R.string.ip_address), serverInfo.ip),
            modifier = Modifier
                .padding(start = padding, top = padding)
                .testTag("serverStatus-Ip"),
            color = Color.White
        )
        Text(
            text = String.format(stringResource(id = R.string.port), serverInfo.port),
            modifier = Modifier
                .padding(start = padding, top = padding, bottom = padding)
                .testTag("serverStatus-Port"),
            color = Color.White
        )
    }
}

@Composable
private fun CallListView(callLogList: List<CallLog>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .testTag("callListView")
    ) {
        items(callLogList) { call ->
            Item(call)
        }
    }
}

@Composable
private fun Item(callLog: CallLog) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(12.dp)
        ) {
            val name: String = callLog.name ?: stringResource(id = R.string.unknown_caller)
            Text(
                text = String.format(stringResource(id = R.string.call_log_name), name),
                modifier = Modifier.testTag("listItem-Name")
            )

            Text(
                text = String.format(
                    stringResource(id = R.string.caller_log_duration),
                    callLog.duration
                ),
                modifier = Modifier.testTag("listItem-Duration")
            )
        }

    }


}