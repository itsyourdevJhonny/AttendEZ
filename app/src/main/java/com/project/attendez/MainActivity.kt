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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.attendez.ui.screens.AttendanceScreen
import com.project.attendez.ui.screens.AttendeeScreen
import com.project.attendez.ui.screens.EventScreen
import com.project.attendez.ui.screens.HistoryScreen
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
        startDestination = "event",
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
        composable("event") {
            EventScreen(
                onEventClick = { eventId -> navController.navigate("attendee/$eventId") },
                onHistory = { navController.navigate("history") }
            )
        }

        composable(
            route = "attendee/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            AttendeeScreen(
                eventId,
                onAttendance = { eventId, attendeeId -> navController.navigate("attendance/$eventId/$attendeeId") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "attendance/{eventId}/{attendeeId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType },
                navArgument("attendeeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
            val attendeeId = backStackEntry.arguments?.getLong("attendeeId") ?: 0L

            AttendanceScreen(eventId, attendeeId, onBack = { navController.popBackStack() })
        }

        composable("history") {
            HistoryScreen { navController.popBackStack() }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AttendEZTheme {
        Greeting("Android")
    }
}