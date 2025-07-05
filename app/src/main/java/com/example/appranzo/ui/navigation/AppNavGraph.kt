
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
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.example.appranzo.ui.navigation.Routes.MAIN
import com.example.appranzo.ui.navigation.Routes.REGISTER
import com.example.appranzo.ui.navigation.Routes.LOGIN
import com.example.appranzo.ui.navigation.Routes.SETTINGS
import com.example.appranzo.ui.screens.FavoritesScreen
import com.example.appranzo.ui.screens.ProfileDetailScreen
import com.example.appranzo.ui.screens.ProfileReviewsScreen
import com.example.appranzo.ui.screens.ProfileScreen
import com.example.appranzo.ui.screens.ReviewScreen
import com.example.appranzo.ui.screens.SearchScreen
import com.example.appranzo.ui.screens.SettingsScreen
import com.example.appranzo.ui.screens.SuccessSearchScreen
import com.example.appranzo.ui.screens.ThemeScreen
import com.example.appranzo.ui.screens.ThemeState
import com.example.appranzo.ui.screens.ThemeViewModel
import com.example.appranzo.viewmodel.ProfileDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = MAIN
    ) {
        // 3) Main container (TopBar + BottomNav con 5 tab)
        composable(MAIN) {
            MainScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.PROFILE_DETAILS) {
            ProfileDetailScreen(navController)
        }

        composable(SETTINGS) {
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
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(Routes.SEARCH_RESULT + "/{categoryId}") { backStack ->
            val catId = backStack.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
            SuccessSearchScreen(
                categoryId = catId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.REVIEW_WITH_ARG,
            arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getInt("restaurantId") ?: return@composable
            ReviewScreen(restaurantId = restaurantId)
        }

        composable(Routes.PROFILE_REVIEWS) {
            ProfileReviewsScreen(navController)
        }


    }
}
