package com.gratelock.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gratelock.app.ui.screen.home.HomeScreen

/**
 * Root navigation host for GrateLock.
 *
 * Wraps all screens in a [Scaffold] with the [BottomNavBar] and manages
 * navigation between the main app destinations.
 */
@Composable
fun GrateLockNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Jar.route) {
                PlaceholderScreen(title = "Happiness Jar")
            }
            composable(Screen.Wrap.route) {
                PlaceholderScreen(title = "Weekly Wrap")
            }
            composable(Screen.Settings.route) {
                PlaceholderScreen(title = "Settings")
            }
        }
    }
}
