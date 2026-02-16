package com.gratelock.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gratelock.app.ui.screen.appselection.AppSelectionScreen
import com.gratelock.app.ui.screen.home.HomeScreen
import com.gratelock.app.ui.screen.jar.EntryDetailScreen
import com.gratelock.app.ui.screen.jar.JarScreen
import com.gratelock.app.ui.screen.onboarding.OnboardingScreen

/** Routes where the bottom nav bar should be hidden. */
private val hideBottomBarRoutes = setOf(
    Screen.AppSelection.route,
    Screen.EntryDetail.route,
    Screen.Onboarding.route,
)

/**
 * Root navigation host for GrateLock.
 *
 * Wraps all screens in a [Scaffold] with the [BottomNavBar] and manages
 * navigation between the main app destinations.
 *
 * @param startDestination The initial route. Defaults to [Screen.Home].
 *   Pass [Screen.Onboarding] for first-time users.
 */
@Composable
fun GrateLockNavHost(startDestination: String = Screen.Home.route) {
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onNavigateToAppSelection = {
                        navController.navigate(Screen.AppSelection.route)
                    },
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAppSelection = {
                        navController.navigate(Screen.AppSelection.route)
                    },
                )
            }
            composable(Screen.Jar.route) {
                JarScreen(
                    onEntryClick = { entryId ->
                        navController.navigate(Screen.EntryDetail.createRoute(entryId))
                    },
                )
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
            composable(
                route = Screen.EntryDetail.route,
                arguments = listOf(
                    navArgument("entryId") { type = NavType.LongType },
                ),
            ) {
                EntryDetailScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
