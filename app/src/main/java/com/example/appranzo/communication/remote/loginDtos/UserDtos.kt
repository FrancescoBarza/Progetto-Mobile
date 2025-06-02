package com.example.appranzo.communication.remote.loginDtos

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestsDtos(
    val username: String,
    val password: String,
    )

@Serializable
data class RegistrationRequestsDtos(
    val username: String,
    val password: String,
    val email: String,
    val photoUrl: String?
)

