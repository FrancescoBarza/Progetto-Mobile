package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.ReviewRequestDto
import com.example.appranzo.communication.remote.loginDtos.ReviewResearchRequestDto
import com.example.appranzo.communication.remote.loginDtos.ReviewDto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Loading : SubmissionState()
    object Success : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}

class ReviewViewModel(
    private val restApiClient: RestApiClient
) : ViewModel() {

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _submitState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submitState: StateFlow<SubmissionState> = _submitState.asStateFlow()


    private val _photos = MutableStateFlow<List<Pair<String, ByteArray>>>(emptyList())
    val photos: StateFlow<List<Pair<String, ByteArray>>> = _photos.asStateFlow()

    fun onRatingChange(value: Int) {
        _rating.value = value
    }

    fun onContentChange(value: String) {
        _content.value = value
    }

    fun addPhoto(image: ByteArray) {
        _photos.value = _photos.value + ("photos" to image)
    }

    fun removePhoto(index: Int) {
        _photos.value = _photos.value.toMutableList().also { it.removeAt(index) }
    }

    fun resetState() {
        _rating.value = 0
        _content.value = ""
        _photos.value = emptyList()
        _submitState.value = SubmissionState.Idle
    }

    fun submitReview(placeId: Int) {
        viewModelScope.launch {
            _submitState.value = SubmissionState.Loading
            try {
                val dto = ReviewRequestDto(
                    placeId = placeId,
                    rating = _rating.value.toByte(),
                    comment = _content.value.takeIf { it.isNotBlank() }
                )
                val success = restApiClient.addReviewWithPhotos(dto, _photos.value)
                _submitState.value = if (success) SubmissionState.Success
                else SubmissionState.Error("Errore durante l'invio")
            } catch (e: Exception) {
                _submitState.value = SubmissionState.Error(e.localizedMessage ?: "Errore sconosciuto")
            }
        }
    }

    suspend fun loadReviews(placeId: Int): List<ReviewDto> {
        return restApiClient.getReviews(ReviewResearchRequestDto(placeId = placeId))
    }
}
