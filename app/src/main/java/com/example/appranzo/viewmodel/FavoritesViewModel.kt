package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.PlaceDto
import com.example.appranzo.data.models.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val restApiClient: RestApiClient
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Place>>(emptyList())
    val favorites: StateFlow<List<Place>> = _favorites.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val dtoList: List<PlaceDto> = restApiClient.getFavorites()
            _favorites.value = dtoList.map { it.toDto() }
        }
    }
    fun removeFavorite(placeId: Int) {
        viewModelScope.launch {
            restApiClient.removeFavorite(placeId)
            loadFavorites()
        }
    }


    fun refresh() = loadFavorites()
}
