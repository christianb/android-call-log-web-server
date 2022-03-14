package com.bunk.server

import android.net.wifi.WifiManager
import android.text.format.Formatter
import com.bunk.server.service.ContentType
import com.bunk.server.service.Service
import com.bunk.server.service.ServiceFactory
import com.bunk.util.DateTimeUtil
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class KtorServer(
    private val wifiManager: WifiManager,
    serviceFactory: ServiceFactory,
) : Server {

    private val isStarted: AtomicBoolean = AtomicBoolean(false)
    private var startTime: String = ""

    private val server: NettyApplicationEngine = embeddedServer(Netty, PORT) {
        routing {
            createGetRoute(serviceFactory.createRootService(startTime, getServerInfo()))
            createGetRoute(serviceFactory.createLogService())
            createGetRoute(serviceFactory.createStatusService())
        }
    }

    private fun Routing.createGetRoute(service: Service): Route {
        return get(service.path) {
            call.respondText(
                service.response(),
                service.contentType.toKtorContentType()
            )
        }
    }

    override fun getServerInfo(): Server.Info = Server.Info(
        ip = getIp(),
        port = getPort(),
        startTime = startTime
    )

    override fun startServer() {
        if (isStarted.compareAndSet(false, true)) {
            startTime = DateTimeUtil.format(
                Calendar.getInstance().timeInMillis,
                DateTimeUtil.DEFAULT_FORMATTER
            )
            server.start(wait = false)
        }
    }

    internal fun stopServer() {
        server.stop(gracePeriodMillis = 0, timeoutMillis = 0)
    }

    private fun getPort(): String = PORT.toString()

    @Suppress("DEPRECATION")
    private fun getIp(): String {
        // I know that connectionInfo.ipAddress is deprecated on WifiManager
        // I tried to find a "simple" way using ConnectivityManager, but couldn't find a good one.
        // However, in real working life I would spend extra time finding a better solution.
        // Same for Formatter.formatIpAddress
        return Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
    }

    private fun ContentType.toKtorContentType(): io.ktor.http.ContentType = when (this) {
        ContentType.Json -> io.ktor.http.ContentType.Application.Json
    }

    companion object {
        private const val PORT = 8080
    }
}