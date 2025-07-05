package com.example.appranzo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appranzo.LoginActivity
import com.example.appranzo.MainActivity
import com.example.appranzo.ui.navigation.Routes
import com.example.appranzo.viewmodel.AuthViewModel
import com.example.appranzo.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val ctx = LocalContext.current
    fun onSuccesfullLogout(navController: NavController,ctx: Context){
        val intent = Intent(ctx, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ctx.startActivity(intent)
    }

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
                        navController.navigate(Routes.PROFILE_DETAILS)
                    }
            )
            Divider()

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
                        navController.navigate(Routes.PROFILE_REVIEWS)
                    }
            )
            Divider()

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
                        viewModel.logOut(onSuccessfullLogout = {onSuccesfullLogout(navController, ctx = ctx)})
                    }
            )
        }
    }
}
