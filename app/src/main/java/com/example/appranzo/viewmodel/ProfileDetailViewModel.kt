package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel(
    private val api: RestApiClient
) : ViewModel() {

    private val _user = MutableStateFlow<UserDto?>(null)
    /** Esponiamo un StateFlow che la UI può collezionare */
    val user: StateFlow<UserDto?> = _user.asStateFlow()

    init {
        loadCurrentUser()
    }

    /** Carica i dettagli dell’utente autenticato e li pubblica su `_user` */
    private fun loadCurrentUser() {
        viewModelScope.launch {

        }
    }

    /** Permette di forzare un refresh manuale dei dati */
    fun refresh() {
        loadCurrentUser()
    }
}
