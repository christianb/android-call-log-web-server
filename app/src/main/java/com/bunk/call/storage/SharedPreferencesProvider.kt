package com.bunk.call.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesProvider(
    private val applicationContext: Context
) {
    fun getSharedPreferences(name: String): SharedPreferences {
        return applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}