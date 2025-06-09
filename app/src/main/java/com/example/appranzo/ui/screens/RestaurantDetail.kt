
package com.example.appranzo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appranzo.ui.theme.APPranzoTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarRate
import com.example.appranzo.data.models.Place

// -------------------
// Data classes
// -------------------

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
    restaurant: Place,
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
                    text = "${restaurant.categoryName} • ${restaurant.address}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f km", restaurant.distanceFromUser),
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
                        text = String.format("%.1f", restaurant.rating),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.width(8.dp))
                    val filledStars = restaurant.rating.toInt()
                    val halfStar = ((restaurant.rating - filledStars) >= 0.5)
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

            val maxCount = 5  //TODO numero recensioni con tot stelle
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                (5 downTo 1).forEach { starValue ->
                    val count = 5
                    RatingBarRow(
                        star = starValue,
                        count = count,
                        maxCount = 10,
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
      /*  items(restaurant.reviews) { review ->
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
        } */

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
private val sampleRestaurant = Place(
    id = 1,
    name = "Trattoria Da Mario",
    categoryName = "Romagnolo",
    address = "Via Roma, 12 – Cesena",
    distanceFromUser = 2.3,
    rating = 4.0,
    description = "ciao",
    city = "Cesena",
    photoUrl = ""
)

