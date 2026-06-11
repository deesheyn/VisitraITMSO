package com.visitraitmso.app

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    private val PH_TZ = TimeZone.getTimeZone("Asia/Manila")

    fun formatTime(ts: Long): String {
        val sdf = SimpleDateFormat("MMM dd · hh:mm a", Locale.getDefault())
        sdf.timeZone = PH_TZ
        return sdf.format(Date(ts))
    }

    fun formatDate(ts: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = PH_TZ
        return sdf.format(Date(ts))
    }

    fun formatHeader(ts: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM dd yyyy · hh:mm a", Locale.getDefault())
        sdf.timeZone = PH_TZ
        return sdf.format(Date(ts))
    }

    fun formatExport(ts: Long): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy  hh:mm a", Locale.getDefault())
        sdf.timeZone = PH_TZ
        return sdf.format(Date(ts))
    }

    fun formatFileName(ts: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        sdf.timeZone = PH_TZ
        return sdf.format(Date(ts))
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        val c1 = Calendar.getInstance(PH_TZ).apply { timeInMillis = t1 }
        val c2 = Calendar.getInstance(PH_TZ).apply { timeInMillis = t2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
}
