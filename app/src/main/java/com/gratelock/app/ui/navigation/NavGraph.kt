package com.gratelock.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gratelock.app.ui.screen.appselection.AppSelectionScreen
import com.gratelock.app.ui.screen.home.HomeScreen

/** Routes where the bottom nav bar should be hidden. */
private val hideBottomBarRoutes = setOf(
    Screen.AppSelection.route,
)

/**
 * Root navigation host for GrateLock.
 *
 * Wraps all screens in a [Scaffold] with the [BottomNavBar] and manages
 * navigation between the main app destinations.
 */
@Composable
fun GrateLockNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomNavBar(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAppSelection = {
                        navController.navigate(Screen.AppSelection.route)
                    },
                )
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
            composable(Screen.AppSelection.route) {
                AppSelectionScreen(
                    onSaveComplete = { navController.popBackStack() },
                )
            }
        }
    }
}
