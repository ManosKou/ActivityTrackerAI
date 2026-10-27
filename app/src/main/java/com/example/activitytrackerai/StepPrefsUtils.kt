package com.example.activitytrackerai

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object StepPrefsUtils {
    private const val PREFS_NAME = "step_prefs"
    private const val KEY_BASELINE = "steps_at_midnight"
    private const val KEY_DATE = "steps_date"

    fun getToday(): String {
        // yyyyMMdd π.χ. 20240523
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    }

    fun saveMidnightSteps(context: Context, baseline: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_BASELINE, baseline)
            .putString(KEY_DATE, getToday())
            .apply()
    }

    fun getMidnightSteps(context: Context): Pair<Int, String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val baseline = prefs.getInt(KEY_BASELINE, 0)
        val date = prefs.getString(KEY_DATE, "19700101") ?: "19700101"
        return Pair(baseline, date)
    }
}