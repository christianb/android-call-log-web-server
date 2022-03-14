package com.bunk.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import com.bunk.R
import com.bunk.call.CallLog
import com.bunk.permission.AppPermission
import com.bunk.server.Server
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(
            this,
            AppPermission.READ_CALL_LOG,
            AppPermission.READ_CONTACTS,
            AppPermission.READ_PHONE_STATE
        )

        setContent {
            val serverInfoState = viewModel.serverInfoLiveData.observeAsState(
                initial = Server.Info(
                    ip = getString(R.string.unknown),
                    port = getString(R.string.unknown),
                    startTime = null
                )
            )

            val callLogState: State<List<CallLog>> =
                viewModel.callLogLiveData.observeAsState(initial = emptyList())

            Screen(serverInfo = serverInfoState.value, callLogList = callLogState.value)
        }
    }

    private fun requestPermissions(activity: Activity, vararg appPermission: AppPermission) {
        val permissions: Array<String> = appPermission.map { it.androidPermission }.toTypedArray()
        ActivityCompat.requestPermissions(activity, permissions, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // very basic permission handling - finish the app with a toast when not all permissions are granted
        permissions.forEachIndexed { index, _ ->
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    R.string.permissions_not_granted,
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return
            }
        }

        viewModel.refresh()
    }
}