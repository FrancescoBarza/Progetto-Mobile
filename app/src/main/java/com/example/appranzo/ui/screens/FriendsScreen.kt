package com.example.appranzo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appranzo.viewmodel.Friend
import com.example.appranzo.viewmodel.FriendRequest
import com.example.appranzo.viewmodel.FriendsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = koinViewModel()
) {
    val friends    by viewModel.friends.collectAsState()
    val requests   by viewModel.requests.collectAsState()
    val codeInput  by viewModel.codeInput.collectAsState()

    Scaffold(

        bottomBar = {
            // BottomAppBar vuoto
            BottomAppBar { }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Sezione “Invia richiesta”
            item {
                Text("Invia richiesta amici", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = viewModel::onCodeChange,
                    label = { Text("Codice univoco") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.sendRequest() },
                ) {
                    Text("Invia")
                }
                Spacer(Modifier.height(24.dp))
            }

            // Sezione “Amici”
            item {
                Text("Amici", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(friends) { friend: Friend ->
                ListItem(
                    headlineContent = { Text(friend.name) },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                HorizontalDivider()
            }
            if (friends.isEmpty()) {
                item {
                    Text("Nessun amico al momento", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))
                }
            } else {
                item { Spacer(Modifier.height(24.dp)) }
            }

            // Sezione “Notifiche”
            item {
                Text("Richieste in sospeso", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            items(requests) { req: FriendRequest ->
                ListItem(
                    headlineContent = { Text(req.name) },
                    supportingContent = { Text("Codice: ${req.id}") },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    trailingContent = {
                        Row {
                            TextButton(onClick = { viewModel.acceptRequest(req) }) {
                                Text("Accetta")
                            }
                            TextButton(onClick = { viewModel.rejectRequest(req) }) {
                                Text("Rifiuta")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
            if (requests.isEmpty()) {
                item {
                    Text("Nessuna richiesta in sospeso", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
