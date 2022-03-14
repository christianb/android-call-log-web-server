package com.bunk.util

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object JsonUtil {

    private val DEFAULT_JSON = Json {
        prettyPrint = true
        encodeDefaults = true

    }

    fun <T> encodeToJsonElement(
        json: Json = DEFAULT_JSON, // I made it so to be more flexible exchanging the Json config for the caller
        serializer: SerializationStrategy<T>,
        value: T
    ): JsonElement = json.encodeToJsonElement(serializer, value)
}