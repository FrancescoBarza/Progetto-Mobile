package com.example.appranzo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appranzo.ui.navigation.Routes.LOGIN
import com.example.appranzo.ui.navigation.Routes.REGISTER
import com.example.appranzo.ui.screens.LoginScreen
import com.example.appranzo.ui.screens.RegisterScreen

@Composable
fun LoginNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
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
    }
}