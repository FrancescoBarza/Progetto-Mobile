
package com.example.appranzo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appranzo.ui.navigation.MainScreen
import com.example.appranzo.ui.screens.LoginScreen
import com.example.appranzo.ui.screens.RegisterScreen
import com.example.appranzo.ui.navigation.Routes
import com.example.appranzo.ui.navigation.Routes.MAIN
import com.example.appranzo.ui.navigation.Routes.REGISTER
import com.example.appranzo.ui.navigation.Routes.LOGIN

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
    }
}
