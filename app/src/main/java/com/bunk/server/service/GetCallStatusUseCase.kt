package com.bunk.server.service

import com.bunk.call.OngoingCall
import com.bunk.call.storage.CallLogStorage
import com.bunk.call.storage.NumberOfCallStorage
import com.bunk.call.storage.OngoingCallStorage

class GetCallStatusUseCase(
    private val ongoingCallStorage: OngoingCallStorage,
    private val numberOfCallStorage: NumberOfCallStorage,
    private val callLogStorage: CallLogStorage
) {
    fun execute(): OngoingCall {
        return ongoingCallStorage.getOngoingCall().also { ongoingCall ->
            if (ongoingCall.ongoing) {
                numberOfCallStorage.incrementTimesQueried(ongoingCall.number)
                callLogStorage.markAsStale()
            }
        }
    }
}