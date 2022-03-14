package com.bunk.call.storage

import com.bunk.call.OngoingCall

interface OngoingCallStorage {
    fun saveOngoingCall(ongoingCall: OngoingCall)
    fun getOngoingCall(): OngoingCall
}

class OngoingCallStorageInMemory : OngoingCallStorage {
    private var ongoingCall: OngoingCall? = null

    override fun saveOngoingCall(ongoingCall: OngoingCall) {
        this.ongoingCall = ongoingCall
    }

    override fun getOngoingCall(): OngoingCall {
        return ongoingCall ?: OngoingCall(ongoing = false, number = null, name = null)
    }

}