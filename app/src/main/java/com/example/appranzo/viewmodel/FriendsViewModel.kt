package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.communication.remote.friendship.FriendshipRequestDto
import com.example.appranzo.communication.remote.loginDtos.UserDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Enum per rappresentare lo stato della UI, utile per mostrare indicatori di caricamento o errori.
 */
enum class UiState {
    IDLE, LOADING, SUCCESS, ERROR
}

/**
 * ViewModel per la gestione della logica della schermata Amici.
 * @param api L'istanza di RestApiClient per comunicare con il backend.
 */
class FriendsViewModel(private val api: RestApiClient) : ViewModel() {

    private val _friends = MutableStateFlow<List<UserDto>>(emptyList())
    val friends = _friends.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<FriendshipRequestDto>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState.asStateFlow()

    private val _usernameInput = MutableStateFlow("")
    val usernameInput = _usernameInput.asStateFlow()

    init {
        // Carica tutti i dati necessari all'avvio del ViewModel.
        fetchAllData()
    }

    /**
     * Aggiorna il valore del campo di input per l'username.
     */
    fun onUsernameChange(newUsername: String) {
        _usernameInput.value = newUsername
    }

    /**
     * Esegue il fetch di tutte le informazioni necessarie per la schermata (amici e richieste)
     * in modo concorrente.
     */
    private fun fetchAllData() {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            try {
                // Esegue le chiamate API in parallelo per una maggiore efficienza.
                val friendsJob = async { fetchFriends() }
                val requestsJob = async { fetchPendingRequests() }
                // Attende il completamento di entrambe.
                friendsJob.await()
                requestsJob.await()
                _uiState.value = UiState.SUCCESS
            } catch (e: Exception) {
                // In caso di errore, imposta lo stato di errore.
                _uiState.value = UiState.ERROR
                println("Errore durante il fetch dei dati: ${e.message}")
            }
        }
    }

    private suspend fun fetchFriends() {
        _friends.update { api.getAllFriends() }
    }

    private suspend fun fetchPendingRequests() {
        _pendingRequests.update { api.getPendingRequests() }
    }

    /**
     * Invia una richiesta di amicizia.
     * La firma dell'API richiede un UserDto, quindi ne creiamo uno "al volo"
     * usando l'username fornito dall'utente.
     */
    fun sendFriendRequest() {
        if (_usernameInput.value.isBlank()) return

        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            // Crea un UserDto fittizio come richiesto dall'API, l'ID non è rilevante qui.
            val userToSendRequest = UserDto(id = 1, username = _usernameInput.value, photoUrl = null)
            val success = api.sendFriendshipRequest(userToSendRequest)

            if (success) {
                _usernameInput.value = "" // Pulisce l'input field
                fetchAllData() // Ricarica i dati per mostrare lo stato aggiornato
            } else {
                _uiState.value = UiState.ERROR
            }
        }
    }

    /**
     * Accetta una richiesta di amicizia.
     */
    fun acceptRequest(request: FriendshipRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val success = api.acceptFriendshipRequest(request)
            if (success) {
                fetchAllData() // Ricarica tutto per aggiornare le liste di amici e richieste
            } else {
                _uiState.value = UiState.ERROR
            }
        }
    }

    /**
     * Rifiuta una richiesta di amicizia.
     */
    fun rejectRequest(request: FriendshipRequestDto) {
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            val success = api.rejectFriendshipRequest(request)
            if (success) {
                // In questo caso basta ricaricare solo la lista delle richieste.
                fetchPendingRequests()
                _uiState.value = UiState.SUCCESS
            } else {
                _uiState.value = UiState.ERROR
            }
        }
    }
}