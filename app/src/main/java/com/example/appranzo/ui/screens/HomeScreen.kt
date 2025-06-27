package com.example.appranzo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appranzo.PlaceDetailActivity
import com.example.appranzo.R
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place
import com.example.appranzo.ui.navigation.BottomNavScreen
import com.example.appranzo.ui.navigation.Routes

fun onClickPlace(place: Place, ctx: Context) {
    val intent = Intent(ctx, PlaceDetailActivity::class.java)
    intent.putExtra("EXTRA_PRODUCT_ID", place.id)
    ctx.startActivity(intent)
}

@Composable
fun HomeScreen(navController: NavController) {
    val ctx = LocalContext.current
    val place = Place(1, "rest", "Top", "Via Bella", "Ortona", null, "pizza", 3.0, 200.0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { navController.navigate(Routes.SEARCH) }
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            CategoryRow(
                modifier = Modifier,
                listOf(
                    Category(1,"pizza"),
                    Category(1,"pizza"),
                    Category(1,"pizza"),
                    Category(1,"pizza"),
                    Category(1,"pizza")
                )
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            MainTitle("Alta Valutazione", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            HighlitedRestaurants(listOf(place, place, place, place)) { p ->
                onClickPlace(p, ctx)
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryTitle("Vicino a Me", modifier = Modifier.padding(horizontal = 16.dp))
                Icon(Icons.Filled.LocalDining, "ArrowIcon")
            }
            Spacer(modifier = Modifier.height(8.dp))
            for (p in listOf(place, place, place, place)) {
                PlaceWithDescription(p, modifier = Modifier) { pl ->
                    onClickPlace(pl, ctx)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,

    ) {
        Text(
            "Cerca",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
        )
    }
}

@Composable
fun CategoryRow(modifier: Modifier = Modifier, categories: List<Category>) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        items(categories) { c ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "${c.name}",
                    contentDescription = "${c.name} photo preview",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.pizzeria),
                    placeholder = painterResource(id = R.drawable.pizzeria)
                )
                Text(
                    "${c.name.capitalize()}",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MainTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun HighlitedRestaurants(
    places: List<Place>,
    onClickColumn: (Place) -> Unit
) {
    LazyRow(
        modifier = Modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(places) { place ->
            var isFavorite by remember { mutableStateOf(false) }
            Box {
                Column(modifier = Modifier.clickable { onClickColumn(place) }) {
                    Card(modifier = Modifier.size(width = 280.dp, height = 180.dp)) {
                        AsyncImage(
                            model = place.photoUrl,
                            contentDescription = place.description,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.restaurantplaceholder),
                            placeholder = painterResource(id = R.drawable.restaurantplaceholder)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        "${place.name.capitalize()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row {
                        repeat(place.rating.toInt()) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Rating icon")
                        }
                    }
                }
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                ) {
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Rimosso dai preferiti",
                            tint = Color.Red,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Aggiunto ai preferiti",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SecondaryTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun PlaceWithDescription(
    place: Place,
    modifier: Modifier = Modifier,
    OnClickPlace: (Place) -> Unit
) {
    val ctx = LocalContext.current
    var isFavorite by remember { mutableStateOf(false) }
    Card(modifier = modifier.fillMaxWidth().clickable { OnClickPlace(place) }) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box {
                AsyncImage(
                    model = place.photoUrl,
                    contentDescription = place.description,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.restaurantplaceholder),
                    placeholder = painterResource(id = R.drawable.restaurantplaceholder)
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(30.dp)
                ) {
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Rimosso dai preferiti",
                            tint = Color.Red,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Aggiunto ai preferiti",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(place.name.capitalize(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    repeat(place.rating.toInt()) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Rating icon")
                    }
                }
                Text(place.description ?: "Place", fontSize = 14.sp)
            }
        }
    }
}
