package com.example.appranzo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.appranzo.PlaceDetailActivity
import com.example.appranzo.R
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var query by remember { mutableStateOf("") }

    // Dummy data
    val samplePlaces = List(5) { idx ->
        Place(
            id = idx,
            name = "Ristorante ${idx + 1}",
            description = "Descrizione $idx",
            address = "Via Roma, $idx",
            city = "Città",
            photoUrl = null,
            categoryName = "Categoria",
            rating = (3..5).random().toDouble(),
            distanceFromUser = (100..2000).random().toDouble()
        )
    }
    val filters = listOf("Miglior voto", "Vicino a te", "Pizza", "Sushi", "Vegano")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Cerca ristorante…") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cerca"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Chiudi ricerca"
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                },
                // niente navigationIcon perché la “X” è all’interno del TextField
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ){ padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { label ->
                        AssistChip(
                            onClick = { /* apply filter */ },
                            label = { Text(label) }
                        )
                    }
                }
            }

            item {
                Text(
                    "Alta Valutazione",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(samplePlaces) { place ->
                        Column(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { startDetail(ctx, place) }
                        ) {
                            Card(
                                modifier = Modifier
                                    .height(120.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                AsyncImage(
                                    model = place.photoUrl,
                                    contentDescription = place.description,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.restaurantplaceholder),
                                    placeholder = painterResource(R.drawable.restaurantplaceholder)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(place.name, fontWeight = FontWeight.Medium)
                            Row {
                                repeat(place.rating.toInt()) {
                                    Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Vicino a te",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.LocalDining, contentDescription = null)
                }
                Spacer(Modifier.height(8.dp))
                samplePlaces.forEach { place ->
                    ListItem(
                        headlineContent = { Text(place.name) },
                        supportingContent = { Text(place.address ?: "") },
                        leadingContent = {
                            AsyncImage(
                                model = place.photoUrl,
                                contentDescription = place.description,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape),
                                error = painterResource(R.drawable.restaurantplaceholder),
                                placeholder = painterResource(R.drawable.restaurantplaceholder)
                            )
                        },
                        modifier = Modifier.clickable { startDetail(ctx, place) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

private fun startDetail(ctx: Context, place: Place) {
    ctx.startActivity(Intent(ctx, PlaceDetailActivity::class.java).apply {
        putExtra("EXTRA_PRODUCT_ID", place.id)
    })
}
