package com.example.appranzo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.appranzo.viewmodel.BadgeRoadViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BadgeRoadScreen(
    viewModel: BadgeRoadViewModel = koinViewModel()
) {
    val points by viewModel.currentPoints.collectAsState(initial = 0)
    val thresholds = viewModel.badgeThresholds
    val reversedList  = thresholds.reversed()
    val linecolor = MaterialTheme.colorScheme.primary
    // dimensioni
    val iconSize: Dp = 40.dp
    val itemHeight: Dp = 100.dp
    val lastIndex   = thresholds.lastIndex
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = lastIndex
    )
    LaunchedEffect(Unit) {
        listState.scrollToItem(lastIndex)
    }
    Scaffold(
        bottomBar = {
            // questo Box resta sempre fisso in basso
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { viewModel.addPoints(10) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = linecolor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        if (points >= thresholds.last()) "Hai raggiunto il massimo"
                        else "Aggiungi 10 punti"
                    )
                }
            }
        }
    ) { innerPadding -> LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,


    ) {
        itemsIndexed(reversedList) { index, threshold ->
            val unlocked = points >= threshold

            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(itemHeight)
                    // disegna la curva che dall'item corrente va al successivo
                    .drawBehind {
                        if (index < thresholds.lastIndex) {
                            val startX = if (index % 2 == 0) iconSize.toPx() / 2f
                            else size.width - iconSize.toPx() / 2f
                            val startY = size.height / 2f
                            val endX = if (index % 2 == 0)
                                size.width - iconSize.toPx() / 2f
                            else iconSize.toPx() / 2f
                            val endY = startY + itemHeight.toPx()

                            val cpX = size.width / 2f        // punto di controllo orizzontale a metà
                            val cpY = (startY + endY) / 2f  // punto di controllo verticale a metà

                            val path = Path().apply {
                                moveTo(startX, startY)
                                quadraticBezierTo(cpX, cpY, endX, endY)
                            }

                            drawPath(
                                path = path,
                                color = linecolor,
                                style = Stroke(width = 8f, cap = StrokeCap.Round)
                            )
                        }
                    }
            ) {
                // allinea la stella a sinistra o destra
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(
                            color = if (unlocked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface,
                            shape = CircleShape
                        )
                        .align(if (index % 2 == 0) Alignment.CenterStart else Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        modifier = Modifier.size(56.dp),
                        contentDescription = "Badge ${index + 1}",
                        tint = if (unlocked)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}}
