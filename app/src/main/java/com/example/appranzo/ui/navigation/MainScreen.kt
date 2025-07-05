package com.example.appranzo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appranzo.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val tabNav = rememberNavController()

    Scaffold(
        topBar = {
            val backStack by tabNav.currentBackStackEntryAsState()
            val currentRoute = backStack?.destination?.route

            val screenTitle = when (currentRoute) {
                Routes.TAB_HOME      -> "APPranzo"
                Routes.TAB_FAVORITES -> "Preferiti"
                Routes.TAB_MAP       -> "Mappa"
                Routes.TAB_FRIENDS   -> "Amici"
                Routes.TAB_BADGES    -> "Badge"
                else -> "APPranzo"
            }

            TopAppBar(
                title = { Text(screenTitle) },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                        Icon(Icons.Filled.Person, contentDescription = "Profilo")
                    }
                    IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Impostazioni")
                    }
                }
            )
        },
        bottomBar = {
            val backStack by tabNav.currentBackStackEntryAsState()
            val current = backStack?.destination?.route
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = current == screen.route,
                        onClick = {
                            tabNav.navigate(screen.route) {
                                popUpTo(tabNav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = tabNav,
            startDestination = Routes.TAB_HOME,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Routes.TAB_HOME)      { HomeScreen(navController) }
            composable(Routes.TAB_FAVORITES) { FavoritesScreen() }
            composable(Routes.TAB_MAP)       { MapScreen() }
            composable(Routes.TAB_FRIENDS)   { FriendsScreen() }
            composable(Routes.TAB_BADGES)    { BadgeRoadScreen() }
        }
    }
}
