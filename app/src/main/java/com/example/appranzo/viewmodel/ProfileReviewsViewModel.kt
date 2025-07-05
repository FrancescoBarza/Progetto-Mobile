package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.ProfileReviewDto
import com.example.appranzo.communication.remote.loginDtos.ReviewDto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileReviewsViewModel(
    private val api: RestApiClient
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ProfileReviewDto>>(emptyList())
    val reviews: StateFlow<List<ProfileReviewDto>> = _reviews.asStateFlow()

    init {
        loadMyReviews()
    }

    private fun loadMyReviews() {
        viewModelScope.launch {
            // recupera le review “grezze”
            val raw = api.getMyReviews()     // chiama /reviews/me
            // estrai tutti gli id unici per non duplicare le chiamate
            val placeIds = raw.map { it.placeId }.toSet()

            // parallelo: per ogni placeId, chiama placeById()
            val lookup = placeIds.map { id ->
                async { id to (api.placeById(id)?.name ?: "—") }
            }.awaitAll().toMap()

            // costruisci la lista UI
            val ui = raw.map { dto ->
                ProfileReviewDto(
                    id = dto.id,
                    placeId = dto.placeId,
                    placeName = lookup[dto.placeId]!!,
                    rating = dto.rating,
                    comment = dto.comment,
                    creationDate = dto.creationDate,
                    username = dto.username,
                    userPhotoUrl = dto.userPhotoUrl
                )
            }
            _reviews.value = ui
        }
    }
}
