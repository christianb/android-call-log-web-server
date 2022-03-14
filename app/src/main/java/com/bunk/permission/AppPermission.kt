package com.bunk.permission

import android.Manifest

enum class AppPermission(
    val androidPermission: String
) {

    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
    READ_CONTACTS(Manifest.permission.READ_CONTACTS),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE)
}