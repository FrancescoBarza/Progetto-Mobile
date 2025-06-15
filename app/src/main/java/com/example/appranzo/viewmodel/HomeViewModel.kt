package com.example.appranzo.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val restApiClient: RestApiClient
) : ViewModel() {
    private val _favIds = MutableStateFlow<Set<Int>>(emptySet())
    val favIds: StateFlow<Set<Int>> = _favIds.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val dtoList = restApiClient.getFavorites()
            _favIds.value = dtoList.map { it.id }.toSet()
        }
    }

    fun toggleFavorite(placeId: Int) {
        viewModelScope.launch {
            if (favIds.value.contains(placeId)) {
                restApiClient.removeFavorite(placeId)
            } else {
                restApiClient.addFavorite(placeId)
            }
            loadFavorites()
        }
    }
}
