package com.bunk.call.storage

import android.content.ContentResolver
import android.database.Cursor
import com.bunk.call.CallLog
import com.bunk.permission.AppPermission
import com.bunk.permission.PermissionHelper
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

interface CallLogStorage {
    fun getCallLogs(): List<CallLog>
    fun markAsStale()
}

class CallLogStorageContentResolver(
    private val contentResolver: ContentResolver,
    private val permissionHelper: PermissionHelper,
    private val numberOfCallStorage: NumberOfCallStorageSharedPreferences,
) : CallLogStorage {

    private val isStale: AtomicBoolean = AtomicBoolean(true)

    /**
     * We do not need to query the contentResolver every time and map all data.
     * As long there was no change to it we can use the cache.
     */
    private var cache: List<CallLog> = mutableListOf()
        set(value) {
            field = Collections.unmodifiableList(value)
        }

    override fun getCallLogs(): List<CallLog> {
        if (!isStale.get()) {
            return cache
        }

        require(permissionHelper.hasPermission(AppPermission.READ_CALL_LOG)) {
            "${AppPermission.READ_CALL_LOG.androidPermission} not granted"
        }

        val mutableCallLogList: MutableList<com.bunk.call.CallLog> = mutableListOf()

        contentResolver.query(
            android.provider.CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            android.provider.CallLog.Calls.DATE + " DESC"
        )?.use { cursor ->
            val numberIndex: Int = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER)
            val nameIndex: Int = cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)
            val dateIndex: Int = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE)
            val durationIndex: Int = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                val phoneNumber: String =
                    cursor.safeGetString(numberIndex).ifEmpty { "unknown number" }
                val callDate: String = cursor.getString(dateIndex)

                mutableCallLogList.add(
                    CallLog.from(
                        beginningInMillis = callDate.toLong(),
                        number = phoneNumber,
                        duration = cursor.getString(durationIndex) ?: "-1",
                        name = cursor.safeGetString(nameIndex).ifEmpty { null },
                        timesQueried = numberOfCallStorage.getTimesQueried(phoneNumber)
                    )
                )
            }
        }

        cache = mutableCallLogList
        isStale.set(false)

        return cache
    }

    override fun markAsStale() {
        isStale.set(true)
    }

    private fun Cursor.safeGetString(index: Int): String = getString(index) ?: ""
}