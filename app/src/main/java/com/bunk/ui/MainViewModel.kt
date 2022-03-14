package com.bunk.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bunk.call.CallLog
import com.bunk.call.storage.CallLogStorage
import com.bunk.server.Server

class MainViewModel(
    server: Server,
    private val callLogStorage: CallLogStorage
) : ViewModel() {

    val serverInfoLiveData: LiveData<Server.Info> = MutableLiveData(server.getServerInfo())

    private val _callLogLiveData: MutableLiveData<List<CallLog>> = MutableLiveData()
    val callLogLiveData: LiveData<List<CallLog>> = _callLogLiveData

    fun refresh() {
        _callLogLiveData.value = callLogStorage.getCallLogs()
    }
}