package com.example.appranzo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appranzo.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // I miei dati
            ListItem(
                headlineContent = { Text("I miei dati") },
                leadingContent = {
                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
            )
            Divider()

            // Le mie recensioni
            ListItem(
                headlineContent = { Text("Le mie recensioni") },
                leadingContent = {
                    Icon(Icons.Default.RateReview, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                    }
            )
            Divider()

            // Disconnetti
            ListItem(
                headlineContent = { Text("Disconnetti") },
                leadingContent = {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                },
                trailingContent = {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN) { inclusive = true }
                        }
                    }
            )
        }
    }
}
