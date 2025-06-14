package com.example.appranzo.viewmodel


import androidx.lifecycle.ViewModel
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileDetailViewModel(
    private val api: RestApiClient
) : ViewModel() {

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    init {
        // qui non serve coroutine, è sincrono
        _user.value = api.getCurrentUserFromToken()
    }
}
