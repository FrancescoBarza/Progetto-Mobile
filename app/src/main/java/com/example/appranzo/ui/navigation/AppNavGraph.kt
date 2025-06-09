
package com.example.appranzo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appranzo.ui.navigation.MainScreen
import com.example.appranzo.ui.screens.LoginScreen
import com.example.appranzo.ui.screens.RegisterScreen
import com.example.appranzo.ui.navigation.Routes
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.appranzo.ui.navigation.Routes.MAIN
import com.example.appranzo.ui.navigation.Routes.REGISTER
import com.example.appranzo.ui.navigation.Routes.LOGIN
import com.example.appranzo.ui.navigation.Routes.SETTINGS
import com.example.appranzo.ui.screens.SettingsScreen
import com.example.appranzo.ui.screens.ThemeScreen
import com.example.appranzo.ui.screens.ThemeState
import com.example.appranzo.ui.screens.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = LOGIN
    ) {
        // 1) Login → LoginScreen gestisce internamente navController.navigate(MAIN)
        composable(LOGIN) {
            LoginScreen(navController = navController)
        }

        // 2) Register → RegisterScreen gestisce internamente navController.navigate(MAIN)
        composable(REGISTER) {
            RegisterScreen(navController = navController)
        }

        // 3) Main container (TopBar + BottomNav con 5 tab)
        composable(MAIN) {
            MainScreen(navController = navController)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        // 4) Sottomenu “Aspetto / Tema”
        composable(Routes.SETTINGS_THEME) {
            // inietto ThemeViewModel da Koin
            val vm: ThemeViewModel = koinViewModel()
            val themeState by vm.state.collectAsStateWithLifecycle()
            ThemeScreen (
                state           = ThemeState(themeState.theme),
                onThemeSelected = { vm.changeTheme(it) },
                onBack = {navController.popBackStack()}
            )
        }
    }
}
