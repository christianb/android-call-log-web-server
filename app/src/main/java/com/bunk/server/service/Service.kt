package com.bunk.server.service

import com.bunk.call.storage.CallLogStorage
import com.bunk.server.Server
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

/**
 * Abstraction for type of services.
 * Independent from any Framework.
 */

interface Service {
    val path: String
    val contentType: ContentType
    fun response(): String
}

class ServiceFactory(
    private val callLogStorage: CallLogStorage,
    private val getCallStatusStatusUseCase: GetCallStatusUseCase
) {
    fun createStatusService(): Service = StatusService(getCallStatusStatusUseCase)
    fun createLogService(): Service = LogService(callLogStorage)
    fun createRootService(startTime: String, serverInfo: Server.Info): Service =
        RootService(startTime, serverInfo)
}

private class StatusService(
    private val getCallStatusStatusUseCase: GetCallStatusUseCase
) : Service {
    override val path: String = PATH
    override val contentType: ContentType = ContentType.Json

    override fun response(): String =
        getCallStatusStatusUseCase.execute().asJsonElement().toString()

    companion object {
        const val PATH = "/status"
    }
}

private class LogService(
    private val callLogStorage: CallLogStorage
) : Service {
    override val path: String = PATH
    override val contentType: ContentType = ContentType.Json

    override fun response(): String {
        val elements: List<JsonElement> = callLogStorage.getCallLogs().map { it.asJsonElement() }
        return JsonArray(elements).toString()
    }

    companion object {
        const val PATH = "/log"
    }
}

private class RootService(
    private val startTime: String,
    serverInfo: Server.Info
) : Service {
    override val path: String = "/"
    override val contentType: ContentType = ContentType.Json

    private val uri = "http://${serverInfo.ip}:${serverInfo.port}"

    private val services = listOf(
        ServiceInfo(name = StatusService.PATH, uri = "$uri${StatusService.PATH}"),
        ServiceInfo(name = LogService.PATH, uri = "$uri${LogService.PATH}")
    )

    override fun response(): String {
        return ServiceInfoList(startTime, services).asJsonElement().toString()
    }
}

sealed class ContentType {
    object Json : ContentType()
}