package com.example.appranzo.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.preference.PreferenceManager
import com.example.appranzo.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Inizializza osmdroid (solo una volta)
    LaunchedEffect(Unit) {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    // Permesso di localizzazione
    val locationPermState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        if (!locationPermState.status.isGranted) {
            locationPermState.launchPermissionRequest()
        }
    }

    if (locationPermState.status.isGranted) {
        OsmdroidMap()
    } else {
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

    // 1) Crea la MapView
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // 2) Overlay per posizione utente
    DisposableEffect(Unit) {
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation()
            enableFollowLocation()
            setDrawAccuracyEnabled(true)
            runOnFirstFix {
                mapView.post {
                    val yourLoc = myLocation
                    mapView.controller.animateTo(yourLoc)
                    mapView.controller.setZoom(18.0)
                }
            }
        }
        mapView.overlays.add(locationOverlay)

        onDispose {
            mapView.overlays.remove(locationOverlay)
        }
    }

    // 3) Overlay per intercettare i tap sulla mappa
    DisposableEffect(Unit) {
        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                // Qui gestisci il tap: per esempio aggiungi un marker
                val marker = Marker(mapView).apply {
                    position = p
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)
                    setOnMarkerClickListener { _, _ ->
                        // click sul marker: mostra un Toast o un dialog
                        Toast.makeText(context, "Marker a: ${"%.5f".format(p.latitude)}, ${"%.5f".format(p.longitude)}", Toast.LENGTH_SHORT).show()
                        true
                    }
                }
                mapView.overlays.add(marker)
                mapView.invalidate()
                return true  // consumiamo l’evento
            }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        })
        mapView.overlays.add(eventsOverlay)

        onDispose {
            mapView.overlays.remove(eventsOverlay)
        }
    }

    // 4) Gestione del lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                else -> { /* no-op */ }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onPause()
        }
    }

    // 5) Inserimento in Compose
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxSize()
    )
}
