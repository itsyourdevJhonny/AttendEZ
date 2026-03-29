package com.project.attendez.navigation

sealed class Routes(val route: String) {
    object Event : Routes("event")
    object Attendee: Routes("attendee")
    object Attendance : Routes("attendance")
    object History : Routes("history")
    object MakeAttendance : Routes("make_attendance")
}