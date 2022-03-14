package com.bunk.server.service

import com.bunk.util.JsonUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ServiceInfo(
    val name: String,
    val uri: String
)

@Serializable
data class ServiceInfoList(
    val start: String,
    val serviceInfos: List<ServiceInfo>
) {
    fun asJsonElement(): JsonElement =
        JsonUtil.encodeToJsonElement(serializer = serializer(), value = this)
}