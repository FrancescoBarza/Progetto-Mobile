package com.example.appranzo.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val surname: String = "",
    val username: String = "",
    val dateOfBirth: String = "",

    val email: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onSurnameChange(value: String) = _state.update { it.copy(surname = value) }
    fun onUsernameChange(value: String) = _state.update { it.copy(username = value) }
    fun onDateOfBirthChange(value: String) = _state.update { it.copy(dateOfBirth = value) }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // TODO: chiamata API con ktor
                kotlinx.coroutines.delay(1000)
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // TODO: chiamata API con ktor
                kotlinx.coroutines.delay(1000)
                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}