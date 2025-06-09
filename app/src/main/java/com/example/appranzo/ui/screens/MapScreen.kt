package com.example.appranzo.ui.screens

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.preference.PreferenceManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Inizializza osmdroid se non l'hai già fatto in Application
    LaunchedEffect(Unit) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    // Gestione permesso di localizzazione
    val locationPermState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        if (!locationPermState.status.isGranted) {
            locationPermState.launchPermissionRequest()
        }
    }

    if (locationPermState.status.isGranted) {
        OsmdroidMap()
    } else {
        // Prompt per chiedere il permesso
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Serve il permesso di localizzazione per mostrare la mappa.")
            Spacer(Modifier.height(8.dp))
            Button(onClick = { locationPermState.launchPermissionRequest() }) {
                Text("Concedi permesso")
            }
        }
    }
}

@Composable
private fun OsmdroidMap() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Crea una sola volta la MapView
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.apply {
                setZoom(15.0)
                setCenter(GeoPoint(0.0, 0.0))
            }
            // Overlay della posizione GPS
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
            locationOverlay.enableMyLocation()
            overlays.add(locationOverlay)
        }
    }

    // Collega onResume/onPause della MapView al Lifecycle di Compose
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    // Inserisci la MapView dentro Compose
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )
}
