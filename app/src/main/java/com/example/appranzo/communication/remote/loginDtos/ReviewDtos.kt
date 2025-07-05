package com.example.appranzo.communication.remote.loginDtos

import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequestDto(
    val placeId: Int,
    val rating: Byte,
    val comment: String? = null
)

@Serializable
data class ReviewResearchRequestDto(
    val placeId: Int,
    val rating: Byte? = null
)


@Serializable
data class ReviewDto(
    val id: Int,
    val placeId: Int,
    val username: String,
    val userPhotoUrl: List<String>,
    val rating: Byte,
    val comment: String,
    val creationDate: String
)

//lo uso per la pagina ProfileReviewsScreen
// mi serve per fare vedere non solo la recensione
// ma anche il nome del posto
@Serializable
data class ProfileReviewDto(
    val id: Int,
    val placeId: Int,
    val placeName: String,
    val username: String,
    val userPhotoUrl: List<String>,
    val rating: Byte,
    val comment: String,
    val creationDate: String

)
