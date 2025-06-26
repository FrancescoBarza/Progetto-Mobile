package com.example.appranzo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.appranzo.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current


    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(
                        WindowInsets
                            .systemBars
                            .asPaddingValues()
                        )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Cerca ristorante…") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cerca"
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi ricerca"
                        )
                    }
                }
            }
        }
    ) {innerPadding->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(3) { category ->
                    ListItem(
                        headlineContent = { Text("nome cat") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                            }
                    )
                    Divider()
                }
            }
        }
    }
}

