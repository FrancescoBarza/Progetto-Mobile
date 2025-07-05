@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.appranzo.ui.screens

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.appranzo.viewmodel.ReviewViewModel
import com.example.appranzo.viewmodel.SubmissionState
import org.koin.androidx.compose.koinViewModel
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    restaurantId: Int,
    viewModel: ReviewViewModel = koinViewModel()
) {
    val rating by viewModel.rating.collectAsStateWithLifecycle()
    val reviewText by viewModel.content.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            val stream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = stream?.readBytes()
            if (bytes != null) {
                viewModel.addPhoto(bytes)
            }
        }
    }

    if (submitState is SubmissionState.Success) {
        LaunchedEffect(Unit) {
            viewModel.resetState()
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lascia una recensione") },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Valutazione", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                (1..5).forEach { star ->
                    IconButton(onClick = { viewModel.onRatingChange(star) }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.StarRate else Icons.Default.StarBorder,
                            contentDescription = null
                        )
                    }
                }
            }

            OutlinedTextField(
                value = reviewText,
                onValueChange = viewModel::onContentChange,
                label = { Text("Recensione") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            Text("Foto", style = MaterialTheme.typography.titleMedium)
            if (photos.isEmpty()) {
                Text("Nessuna foto selezionata.")
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    photos.forEachIndexed { index, (_, byteArray) ->
                        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto $index",
                            modifier = Modifier
                                .size(64.dp)
                                .clickable { viewModel.removePhoto(index) }
                        )
                    }
                }
            }

            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Aggiungi immagini")
            }

            Button(
                onClick = { viewModel.submitReview(restaurantId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = submitState !is SubmissionState.Loading
            ) {
                if (submitState is SubmissionState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Invia recensione")
                }
            }

            if (submitState is SubmissionState.Success) {
                LaunchedEffect(Unit) {
                    viewModel.resetState()
                    (context as? Activity)?.finish()
                }
            }

        }
    }
}
