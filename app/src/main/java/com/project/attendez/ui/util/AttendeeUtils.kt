package com.project.attendez.ui.util

object AttendeeUtils {
    fun getYearLevel(yearLevel: Int?): String {
        return "${
            when (yearLevel) {
                1 -> "1st"
                2 -> "2nd"
                3 -> "3rd"
                else -> "4th"
            }
        } Year"
    }
}