package com.bunk.call

import com.bunk.util.DateTimeUtil
import com.bunk.util.JsonUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CallLog(
    val beginning: String,
    val duration: String,
    val number: String,
    val name: String?,
    val timesQueried: Int
) {

    fun asJsonElement(): JsonElement =
        JsonUtil.encodeToJsonElement(serializer = serializer(), value = this)

    companion object {
        fun from(
            beginningInMillis: Long,
            duration: String,
            number: String,
            name: String?,
            timesQueried: Int
        ): CallLog = CallLog(
            beginning = DateTimeUtil.format(
                beginningInMillis,
                formatter = DateTimeUtil.DEFAULT_FORMATTER
            ),
            duration = duration,
            number = number,
            name = name,
            timesQueried = timesQueried
        )
    }
}