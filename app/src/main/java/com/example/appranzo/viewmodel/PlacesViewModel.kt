package com.example.appranzo.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place
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
    val favouritePlaces: List<Place> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
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
        viewModelScope.launch {
            val favorites= restApiClient.getFavorites()
            _homePageState.update { it.copy(favouritePlaces = favorites) }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _homePageState.update { it.copy(categories = restApiClient.getCategories()) }
        }
    }

    fun loadNearPlaces(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _homePageState.update { it.copy(nearPlaces = restApiClient.getNearRestaurants(latitude, longitude), isLoading = false) }
        }
    }

     fun toggleFavourites(place:Place){
        val a = viewModelScope.launch {
            restApiClient.toggleFavourite(placeId = place.id)
        }
    }

    fun getLastLocation() {
        val context = getApplication<Application>().applicationContext
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            _homePageState.update { it.copy(isLoading = true, error = null) }
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    loadNearPlaces(it.latitude, it.longitude)
                } ?: run {
                    _homePageState.update { it.copy(error = "Could not get last known location. Make sure GPS is enabled.", isLoading = false) }
                }
            }.addOnFailureListener { e ->
                _homePageState.update { it.copy(error = "Error getting location: ${e.message}", isLoading = false) }
            }
        } else {
            _homePageState.update { it.copy(error = "Location permission not granted.", isLoading = false) }
        }
    }
}