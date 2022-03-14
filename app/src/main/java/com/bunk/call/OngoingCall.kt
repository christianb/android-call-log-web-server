package com.bunk.call

import com.bunk.util.JsonUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OngoingCall(
    val ongoing: Boolean,
    val number: String?,
    val name: String?,
) {
    fun asJsonElement(): JsonElement =
        JsonUtil.encodeToJsonElement(serializer = serializer(), value = this)
}
