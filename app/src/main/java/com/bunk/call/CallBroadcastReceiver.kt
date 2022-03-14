package com.bunk.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup
import android.telephony.TelephonyManager
import com.bunk.call.storage.CallLogStorage
import com.bunk.call.storage.OngoingCallStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CallBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    private val ongoingCallStorage: OngoingCallStorage by inject()
    private val calLogStorage: CallLogStorage by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "android.intent.action.PHONE_STATE") return
        // I know this BroadcastReceiver gets called twice when the phoneState changes
        // I suspect that some data within the intent (for the same action) is different
        // For this code challenge I keep it so, however in real work life I would taking extra time fixing this issue.

        // I am aware that EXTRA_INCOMING_NUMBER is deprecated.
        // However, for this code challenge, I am not going to implement CallScreeningService API because of its complexity.
        // In real working life I would consider doing it the right way.
        @Suppress("DEPRECATION")
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val isOngoingCall = isOngoingCall(intent)

        // There was no specification what to show when there is no ongoing call.
        // So when there is no ongoing call number and name are null
        val ongoingCall = OngoingCall(
            ongoing = isOngoingCall,
            number = if (isOngoingCall) incomingNumber else null,
            name = if (isOngoingCall) getCallerName(incomingNumber, context) else null
        )
        ongoingCallStorage.saveOngoingCall(ongoingCall)
        calLogStorage.markAsStale()
    }

    private fun getCallerName(phoneNumber: String?, context: Context): String? {
        phoneNumber ?: return null
        val uri: Uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
        context.contentResolver.query(
            uri,
            arrayOf(PhoneLookup.DISPLAY_NAME),
            phoneNumber,
            null,
            null
        )?.use { cursor: Cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME)
                if (index < 0) return null
                return cursor.getString(index)
            }
        }

        return null
    }

    private fun getState(intent: Intent): String? =
        intent.getStringExtra(TelephonyManager.EXTRA_STATE)

    private fun isOngoingCall(intent: Intent): Boolean {
        val state: String? = getState(intent)
        return state == TelephonyManager.EXTRA_STATE_OFFHOOK || state == TelephonyManager.EXTRA_STATE_RINGING
    }
}