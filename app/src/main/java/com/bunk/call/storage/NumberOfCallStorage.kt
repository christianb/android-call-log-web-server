package com.bunk.call.storage

import android.content.SharedPreferences
import androidx.core.content.edit

interface NumberOfCallStorage {
    fun getTimesQueried(phoneNumber: String): Int
    fun incrementTimesQueried(phoneNumber: String?)
}

class NumberOfCallStorageSharedPreferences(sharedPreferencesProvider: SharedPreferencesProvider) :
    NumberOfCallStorage {
    private val preferences: SharedPreferences =
        sharedPreferencesProvider.getSharedPreferences(NAME)

    override fun getTimesQueried(phoneNumber: String): Int = preferences.getInt(phoneNumber, 0)

    override fun incrementTimesQueried(phoneNumber: String?) {
        phoneNumber ?: return
        preferences.edit {
            putInt(phoneNumber, getTimesQueried(phoneNumber) + 1)
        }
    }

    companion object {
        private const val NAME = "Call_Log_Counter"
    }
}