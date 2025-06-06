package com.example.appranzo.viewmodel


import android.content.Context
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.loginDtos.AuthResults
import com.example.appranzo.communication.remote.loginDtos.LoginErrorReason
import com.example.appranzo.communication.remote.loginDtos.RegistrationErrorReason
import com.example.appranzo.data.repository.TokensRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class AuthUiState(
    val name: String = "",
    val surname: String = "",
    val username: String = "",

    val email: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val restApiClient: RestApiClient,private val tokensRepository: TokensRepository) : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onSurnameChange(value: String) = _state.update { it.copy(surname = value) }
    fun onUsernameChange(value: String) = _state.update { it.copy(username = value) }


    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }


    fun login(onSuccess: () -> Unit, onFailureDisplay: (message: String) -> Unit, ctx: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentValues = _state.value
            val username = currentValues.username
            val password = currentValues.password
            _state.update { it.copy(isLoading = true) }
            try {
                if (RestApiClient.isOnline(ctx)) {
                    val result = restApiClient.login(username, password)
                    when (result) {
                        is AuthResults.TokenDtos -> {
                            addTokens(result.accessToken,result.refreshToken)
                            onSuccess()
                            _state.update { it.copy(error = result.refreshToken) }

                        }

                        is AuthResults.ErrorLoginResponseDto -> {
                            val resultStatus = result.errorSignal
                            val optionalError = result.optionalMessage
                            when (resultStatus) {
                                LoginErrorReason.CREDENTIALS_INVALID -> {
                                    optionalError?.let { onFailureDisplay(it) }
                                    _state.update { it.copy(error = "Invalid Credentials") }
                                }

                                LoginErrorReason.DATABASE_ERROR -> {
                                    optionalError?.let { onFailureDisplay(it) }
                                    _state.update { it.copy(error = "Server Error") }
                                }

                                LoginErrorReason.INTERNAL_ERROR -> {
                                    optionalError?.let { onFailureDisplay(it) }
                                    _state.update { it.copy(error = "Internal Error") }
                                }
                            }
                            _state.update { it.copy(isLoading = false) }
                        }

                        else -> {
                            onFailureDisplay("unexpected result")
                            _state.update { it.copy(error = "Unexpected Error") }
                        }
                    }

                } else {
                    RestApiClient.openWirelessSettings(ctx)
                }
            } catch (e: Error) {
                onFailureDisplay(e.message ?: "Error")
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun register(onSuccess: () -> Unit, ctx: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentValues = _state.value
            val username = currentValues.username
            val password = currentValues.password
            val email = currentValues.email
            try {
                if (username.isBlank()) {
                    throw Error("please choose a valid username")
                }
                if (email.isBlank()) {
                    throw Error("please choose a valid email")
                }
                when {
                    password.isEmpty() -> {
                        throw Error("please choose a valid password")
                    }

                    password.length < 8 -> {
                        throw Error("your password must be at less 8 characters long")
                    }

                    password.none { it.isUpperCase() } -> {
                        throw Error("your password must contain at least one uppercase letter")
                    }

                    password.none { it.isLowerCase() } -> {
                        throw Error("your password must contain at least one lowercase letter")
                    }

                    password.none { it.isDigit() } -> {
                        throw Error("password must contain at least one digit")
                    }

                    password.all { it.isLetterOrDigit() } -> {
                        throw Error("password must contain at least one special character")
                    }
                }
                if (RestApiClient.isOnline(ctx)) {
                    val result = restApiClient.register(username, password,email)

                    when (result) {
                        is AuthResults.TokenDtos -> {
                            addTokens(result.accessToken,result.refreshToken)
                            onSuccess()
                            _state.update { it.copy(error = result.refreshToken) }
                        }

                        is AuthResults.ErrorRegistrationResponseDto -> {
                            when (result.errorSignal) {
                                RegistrationErrorReason.EMAIL_TAKEN -> _state.update {
                                    it.copy(error ="Email address is already taken")}
                                RegistrationErrorReason.PASSWORD_INVALID -> _state.update {
                                    it.copy(error ="The password provided is invalid")}
                                RegistrationErrorReason.USERNAME_TAKEN -> _state.update {
                                    it.copy(error ="Username is already taken")}
                                RegistrationErrorReason.USERNAME_INVALID -> _state.update {
                                    it.copy(error ="The username provided is invalid")}
                                else ->_state.update {
                                    it.copy(error ="Internal error occured")}
                            }
                            _state.update { it.copy(error = "Unexpected Error") }

                        }

                        else -> {
                            _state.update { it.copy(error = "Unexpected Error") }
                        }
                    }


                } else {
                    RestApiClient.openWirelessSettings(ctx)
                }
            } catch (e: Error) {
                _state.update {
                    it.copy(error = e.message?:"Unexpected Error")
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun addTokens(accessToken :String, refreshToken:String){
        viewModelScope.launch {
            tokensRepository.changeTokens(accessToken, refreshToken)
        }
    }
}