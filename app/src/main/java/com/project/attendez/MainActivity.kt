package com.project.attendez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.attendez.navigation.Routes
import com.project.attendez.ui.screens.AttendanceScreen
import com.project.attendez.ui.screens.AttendeeScreen
import com.project.attendez.ui.screens.EventScreen
import com.project.attendez.ui.screens.HistoryScreen
import com.project.attendez.ui.screens.MakeAttendanceScreen
import com.project.attendez.ui.theme.AttendEZTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendEZTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Event.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable(Routes.Event.route) {
            EventScreen(
                onEventClick = { eventId -> navController.navigate(Routes.Attendee.route + "/$eventId") },
                onHistory = { navController.navigate(Routes.History.route) }
            )
        }

        composable(
            route = Routes.Attendee.route + "/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            AttendeeScreen(
                eventId,
                onAttendance = { eventId, attendeeId -> navController.navigate(Routes.Attendance.route + "/$eventId/$attendeeId") },
                onMakeAttendance = { navController.navigate(Routes.MakeAttendance.route + "/$eventId") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.Attendance.route + "/{eventId}/{attendeeId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType },
                navArgument("attendeeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
            val attendeeId = backStackEntry.arguments?.getLong("attendeeId") ?: 0L

            AttendanceScreen(eventId, attendeeId, onBack = { navController.popBackStack() })
        }

        composable(Routes.History.route) {
            HistoryScreen { navController.popBackStack() }
        }

        composable(
            route = Routes.MakeAttendance.route + "/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            MakeAttendanceScreen(
                eventId,
                onAttendance = { eventId, attendeeId -> navController.navigate(Routes.Attendance.route + "/$eventId/$attendeeId") },
                onBack = { navController.popBackStack() }
            )
        }
    }
}