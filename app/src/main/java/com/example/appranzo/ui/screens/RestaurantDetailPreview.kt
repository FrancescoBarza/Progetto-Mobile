
package com.example.appranzo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appranzo.ui.theme.APPranzoTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarRate
import androidx.navigation.compose.rememberNavController
import com.example.appranzo.ui.navigation.AppNavGraph

// -------------------
// Data classes
// -------------------
data class Restaurant(
    val name: String,
    val cuisine: String,
    val address: String,
    val distanceKm: Double,
    val averageRating: Double,       // da 0.0 a 5.0
    val ratingCounts: Map<Int, Int>, // chiave = stelle (1..5), valore = numero di recensioni
    val reviews: List<Review>
)

data class Review(
    val author: String,
    val date: String,      // es. "12/05/2025"
    val rating: Int,       // da 1 a 5
    val content: String
)

// ----------------------------------------------------
// Composable principale (contenuto senza ViewModel/Nav)
// ----------------------------------------------------
@Composable
fun RestaurantDetailContent(
    restaurant: Restaurant,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Immagine di copertina (qui usiamo un'icona di placeholder)
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Placeholder Immagine",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        // Nome, cucina, indirizzo e distanza
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${restaurant.cuisine} • ${restaurant.address}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f km", restaurant.distanceKm),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }

        // Valutazione media e stelle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Valutazione media",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.1f", restaurant.averageRating),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.width(8.dp))
                    val filledStars = restaurant.averageRating.toInt()
                    val halfStar = ((restaurant.averageRating - filledStars) >= 0.5)
                    for (i in 1..5) {
                        val icon = when {
                            i <= filledStars -> Icons.Default.StarRate // qui potresti cambiare con ic_star_filled
                            i == filledStars + 1 && halfStar -> Icons.Default.StarHalf // placeholder mezza stella
                            else -> Icons.Default.StarBorder // placeholder stella vuota
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Distribuzione punteggi (grafico a barre)
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Distribuzione voti",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))

            val maxCount = remember(restaurant.ratingCounts) {
                restaurant.ratingCounts.values.maxOrNull() ?: 1
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                (5 downTo 1).forEach { starValue ->
                    val count = restaurant.ratingCounts[starValue] ?: 0
                    RatingBarRow(
                        star = starValue,
                        count = count,
                        maxCount = maxCount,
                        barHeight = 12.dp,
                        barColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        // Sezione “Recensioni”
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Recensioni",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        // Elenco delle recensioni
        items(restaurant.reviews) { review ->
            ReviewItem(review)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
        item {
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* Azione per lasciare recensione */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Lascia una recensione")
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 8) Spazio finale per non coprire il contenuto
        item {
            Spacer(Modifier.height(80.dp))
        }
    }
}

// --- Composable di supporto: riga con stella + barra orizzontale proporzionale ---
@Composable
private fun RatingBarRow(
    star: Int,
    count: Int,
    maxCount: Int,
    barHeight: Dp,
    barColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$star",
            modifier = Modifier.width(24.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Default.StarRate, // placeholder stella piena
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))

        val fraction = if (maxCount > 0) count.toFloat() / maxCount else 0f
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(color = barColor, shape = CircleShape)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text("($count)", style = MaterialTheme.typography.bodySmall)
    }
}

// --- Composable di supporto: singola recensione ---
@Composable
private fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = review.date,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
        Spacer(Modifier.height(4.dp))
        Row {
            repeat(review.rating) {
                Icon(
                    imageVector = Icons.Default.StarRate, // placeholder stella piena
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            repeat(5-review.rating){
                Icon(
                    imageVector = Icons.Default.StarBorder, // placeholder stella piena
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = review.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}

// ----------------------
// Preview di sola UI
// ----------------------
private val sampleRestaurant = Restaurant(
    name = "Trattoria Da Mario",
    cuisine = "Romagnolo",
    address = "Via Roma, 12 – Cesena",
    distanceKm = 2.3,
    averageRating = 4.2,
    ratingCounts = mapOf(
        5 to 40,
        4 to 15,
        3 to 5,
        2 to 2,
        1 to 1
    ),
    reviews = listOf(
        Review("Laura", "01/05/2025", 5, "Cappelletti squisiti, atmosfera calda."),
        Review("Marco", "15/04/2025", 4, "Buono ma un po’ caro."),
        Review("Sara",  "10/04/2025", 2, "Male Male.")
    )
)

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RestaurantDetailPreview() {
    APPranzoTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
        RestaurantDetailContent(
            restaurant = sampleRestaurant,
            innerPadding = PaddingValues(0.dp)
        )
    }
    }
}
