package com.example.appranzo.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel // Change this import
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place
import com.example.appranzo.data.repository.PlacesRepository // If you're not using this, you can remove it
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomePageState(
    val categories: List<Category> = emptyList(),
    val nearPlaces: List<Place> = emptyList(),
    val isLoading: Boolean = false, // Add isLoading state for better UI feedback
    val error: String? = null // Add error state for better UI feedback
)

class PlacesViewModel(private val restApiClient: RestApiClient, application: Application) : AndroidViewModel(application) {
    private val _homePageState = MutableStateFlow(HomePageState())
    val homePageState = _homePageState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomePageState()
    )
    private val fusedLocationClient: FusedLocationProviderClient

    init {
        loadCategories()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    }

    fun loadCategories() {
        viewModelScope.launch {
            _homePageState.update { it.copy(categories = restApiClient.getCategories()) }
        }
    }

    fun loadNearPlaces(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _homePageState.update { it.copy(nearPlaces = restApiClient.getNearRestaurants(latitude, longitude), isLoading = false) } // Update isLoading
        }
    }

    fun getLastLocation() {
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            _homePageState.update { it.copy(isLoading = true, error = null) } // Set loading state
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    loadNearPlaces(it.latitude, it.longitude)
                } ?: run {
                    _homePageState.update { it.copy(error = "Could not get last known location. Make sure GPS is enabled.", isLoading = false) } // Update error and loading
                }
            }.addOnFailureListener { e ->
                _homePageState.update { it.copy(error = "Error getting location: ${e.message}", isLoading = false) } // Update error and loading
            }
        } else {
            _homePageState.update { it.copy(error = "Location permission not granted.", isLoading = false) } // Update error and loading
        }
    }
}