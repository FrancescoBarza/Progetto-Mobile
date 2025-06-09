package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.data.models.Category
import com.example.appranzo.data.models.Place
import com.example.appranzo.data.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

data class HomePageState(
    val categories:List<Category> = emptyList(),
    val places:List<Place> = emptyList(),

)

class PlacesViewModel(placesRepository: PlacesRepository):ViewModel(){
    private val _placesState = MutableStateFlow(HomePageState())
    val placesState = _placesState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Place(id=1,name="restaurant", description = "description", city = "City", photoUrl = null, categoryName = "pizza", rating = 3.0, distanceFromUser = 500.0)
    )

}