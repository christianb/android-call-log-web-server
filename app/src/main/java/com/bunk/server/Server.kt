package com.bunk.server

interface Server {
    fun getServerInfo(): Info
    fun startServer()

    data class Info(
        val ip: String,
        val port: String,
        val startTime: String?,
    )
}