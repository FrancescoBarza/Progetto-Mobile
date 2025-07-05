package com.example.appranzo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appranzo.communication.remote.friendship.FriendshipRequestDto
import com.example.appranzo.communication.remote.loginDtos.UserDto
import com.example.appranzo.viewmodel.FriendsViewModel
import com.example.appranzo.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = koinViewModel()
) {
    val friends by viewModel.friends.collectAsState()
    val pendingRequests by viewModel.pendingRequests.collectAsState()
    val usernameInput by viewModel.usernameInput.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val isLoading = uiState == UiState.LOADING

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Invia richiesta amico", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = viewModel::onUsernameChange,
                        label = { Text("Username dell'amico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.sendFriendRequest() },
                                enabled = !isLoading && usernameInput.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Invia richiesta"
                                )
                            }
                        },
                        readOnly = isLoading
                    )
                }

                item {
                    Text("Richieste in sospeso", style = MaterialTheme.typography.titleLarge)
                }
                if (pendingRequests.isEmpty()) {
                    item {
                        Text("Nessuna richiesta in sospeso.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    items(pendingRequests, key = { it.id }) { request ->
                        FriendRequestItem(
                            request = request,
                            onAccept = { viewModel.acceptRequest(request) },
                            onReject = { viewModel.rejectRequest(request) },
                            isLoading = isLoading
                        )
                        Divider()
                    }
                }
                item {
                    Text("I tuoi Amici", style = MaterialTheme.typography.titleLarge)
                }
                if (friends.isEmpty()) {
                    item {
                        Text("Non hai ancora nessun amico.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    items(friends, key = { it.id }) { friend ->
                        FriendItem(friend = friend)
                        Divider()
                    }
                }
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun FriendItem(friend: UserDto) {
    ListItem(
        headlineContent = { Text(friend.username) },
        leadingContent = {
            Icon(Icons.Default.Person, contentDescription = "Icona Amico")
        }
    )
}

@Composable
private fun FriendRequestItem(
    request: FriendshipRequestDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    isLoading: Boolean
) {
    ListItem(
        headlineContent = {
            Text("Richiesta da Utente #${request.senderId}")
        },
        leadingContent = {
            Icon(Icons.Default.Person, contentDescription = "Icona Richiesta")
        },
        trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onAccept, enabled = !isLoading) {
                    Text("Accetta")
                }
                TextButton(onClick = onReject, enabled = !isLoading) {
                    Text("Rifiuta")
                }
            }
        }
    )
}