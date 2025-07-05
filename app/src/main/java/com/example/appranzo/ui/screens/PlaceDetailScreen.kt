package com.example.appranzo.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appranzo.communication.remote.loginDtos.ReviewDto
import com.example.appranzo.data.models.Place
import com.example.appranzo.viewmodel.PlaceDetailViewModel
import com.example.appranzo.viewmodel.RestaurantDetailActualState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import com.example.appranzo.ReviewActivity
import com.example.appranzo.ui.navigation.Routes


@Composable
fun RestaurantDetailContent(
    restaurantId: Int,
    viewModel: PlaceDetailViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val detailState by viewModel.state.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val isReviewLoading by viewModel.isReviewsLoading.collectAsStateWithLifecycle()

    LaunchedEffect(restaurantId) {
        viewModel.loadRestaurantById(restaurantId)
        viewModel.loadReviews(restaurantId)
    }

    when (val actualState = detailState) {
        is RestaurantDetailActualState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is RestaurantDetailActualState.Success -> {
            val restaurant = actualState.restaurant

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Gray)
                    ) {
                        IconButton(
                            onClick = {
                                if (context is androidx.activity.ComponentActivity) {
                                    context.finish()
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp, 56.dp, 16.dp, 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Indietro",
                                tint = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Placeholder Immagine",
                            tint = Color.White,
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center)
                        )
                    }
                }


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
                                    i <= filledStars -> Icons.Default.StarRate
                                    i == filledStars + 1 && halfStar -> Icons.Default.StarHalf
                                    else -> Icons.Default.StarBorder
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

                item {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Distribuzione voti",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))

                    val starCounts = (1..5).associateWith { star ->
                        reviews.count { it.rating.toInt() == star }
                    }

                    val maxCount = starCounts.values.maxOrNull() ?: 1

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        (5 downTo 1).forEach { starValue ->
                            val count = starCounts[starValue] ?: 0
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

                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Recensioni",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (isReviewLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                } else {
                    if (reviews.isEmpty()) {
                        item {
                            Text(
                                text = "Nessuna recensione disponibile.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {

                        items(reviews) { review ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                ReviewItem(
                                    review = Review(
                                        author  = review.username,
                                        date    = review.creationDate,
                                        rating  = review.rating.toInt(),
                                        content = review.comment
                                    )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Vedi in dettaglio →",
                                    style = MaterialTheme.typography.bodyMedium
                                        .copy(color = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .clickable {
                                            // TODO: apri dettaglio recensione
                                        }
                                )
                            }
                            Divider(Modifier.padding(vertical = 8.dp))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(context, ReviewActivity::class.java)
                                intent.putExtra("restaurantId", restaurantId)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(text = "Lascia una recensione")
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        is RestaurantDetailActualState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Impossible to load this restaurant")
            }
        }
    }
}

@Composable
fun RatingBarRow(
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
            imageVector = Icons.Default.StarRate,
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

@Composable
fun ReviewItem(review: Review) {
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
                    imageVector = Icons.Default.StarRate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            repeat(5 - review.rating) {
                Icon(
                    imageVector = Icons.Default.StarBorder,
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

data class Review(
    val author: String,
    val date: String,
    val rating: Int,
    val content: String
)
