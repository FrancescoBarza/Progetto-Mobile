package com.example.appranzo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.appranzo.data.models.Theme
import com.example.appranzo.ui.navigation.AppNavGraph
import com.example.appranzo.ui.screens.RestaurantDetailContent
import com.example.appranzo.ui.screens.ThemeViewModel
import com.example.appranzo.ui.theme.APPranzoTheme
import org.koin.androidx.compose.koinViewModel

class PlaceDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedId = intent.getIntExtra("EXTRA_PRODUCT_ID", -1)
        setContent {
            PlaceDetail(receivedId)
        }
    }
}

@Composable
fun PlaceDetail(id:Int) {
    // 1) Recupera ThemeViewModel da Koin
    val themeViewModel: ThemeViewModel = koinViewModel()

    // 2) Leggi lo stato corrente (Light, Dark, System)
    val themeState by themeViewModel.state.collectAsStateWithLifecycle()

    // 3) “Converti” AppTheme (enum) in boolean per il parametro darkTheme
    val darkModeEnabled = when (themeState.theme) {
        Theme.Light -> false
        Theme.Dark -> true
        Theme.System -> isSystemInDarkTheme()
    }
    APPranzoTheme(darkTheme = darkModeEnabled) {

        Surface(color = MaterialTheme.colorScheme.background) {
            RestaurantDetailContent(id, koinViewModel())
        }
    }
}