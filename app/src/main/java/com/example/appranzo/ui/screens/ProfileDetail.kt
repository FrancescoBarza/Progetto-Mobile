package com.example.appranzo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appranzo.viewmodel.ProfileDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    navController: NavController,
    viewModel: ProfileDetailViewModel = koinViewModel()
) {
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettagli Profilo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (user == null) {
                Text(
                    "Utente non autenticato",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    user!!.photoUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Foto profilo",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } ?: Icon(
                        Icons.Default.Person,
                        contentDescription = "Placeholder foto",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Text(
                        "Username: ${user!!.username}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "ID: ${user!!.id}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}