package com.bunk.util

import android.annotation.SuppressLint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    // I know about the problem that assigning the default locale to a final field
    // will not change the locale if the user changes the locale while the app is running.
    // In real work life I would invest more time fixing this issue.
    @SuppressLint("ConstantLocale")
    val DEFAULT_FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

    fun format(dateInMillis: Long, formatter: DateFormat): String =
        formatter.format(Date(dateInMillis))
}