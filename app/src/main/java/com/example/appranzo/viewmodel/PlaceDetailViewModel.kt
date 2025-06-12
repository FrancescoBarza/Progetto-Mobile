package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.data.models.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface RestaurantDetailActualState{
    object Loading : RestaurantDetailActualState
    data class Success(val restaurant: Place) : RestaurantDetailActualState
    data class Error(val message: String) : RestaurantDetailActualState
}

class PlaceDetailViewModel(
    private val restApiClient: RestApiClient
) : ViewModel() {
    private val _state = MutableStateFlow<RestaurantDetailActualState>(RestaurantDetailActualState.Loading)
            val state:StateFlow<RestaurantDetailActualState> = _state

    fun loadRestaurantById(id:Int){
        if (_state.value !is RestaurantDetailActualState.Loading) {
            _state.value = RestaurantDetailActualState.Loading
        }

        viewModelScope.launch {
            try {
                val place = restApiClient.placeById(id)
                if(place!=null){_state.value = RestaurantDetailActualState.Success(place)}
                else _state.value=RestaurantDetailActualState.Error("Error while loading place")
            } catch (e: Exception) {
                _state.value=RestaurantDetailActualState.Error("Error while loading place")
            }
        }
    }
}