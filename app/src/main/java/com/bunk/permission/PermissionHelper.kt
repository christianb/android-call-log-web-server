package com.bunk.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionHelper(
    private val applicationContext: Context,
) {
    fun hasPermission(appPermission: AppPermission): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            appPermission.androidPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
}