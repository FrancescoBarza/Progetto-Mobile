package com.example.appranzo.communication.remote.loginDtos

import kotlinx.serialization.SerialName
import kotlinx. serialization. Serializable;

@Serializable
data class PositionDto(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class PlaceDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val city: String,
    val address: String? = null,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String? = null,
    val categoryId: Int,
    val distanceFromUser: Double? = null
)

@Serializable
data class ReverseGeocodingResponse(
    val address: AddressDto
)

@Serializable
data class AddressDto(
    @SerialName("road")
    val road: String? = null,
    @SerialName("house_number")
    val houseNumber: String? = null
)

@Serializable
data class ResearchDto(
    val latitude: Double?,
    val longitude: Double?,
    val researchInput: String
)