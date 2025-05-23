package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class Friend(val id: String, val name: String)
data class FriendRequest(val id: String, val name: String)

class FriendsViewModel : ViewModel() {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    private val _requests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val requests: StateFlow<List<FriendRequest>> = _requests

    private val _codeInput = MutableStateFlow("")
    val codeInput: StateFlow<String> = _codeInput

    fun onCodeChange(new: String) {
        _codeInput.value = new
    }

    fun sendRequest() {
        // Simula l'invio della richiesta: qui aggiungiamo in requests
        val newReq = FriendRequest(
            id = codeInput.value,
            name = "User-${codeInput.value.takeLast(4)}"
        )
        _requests.update { it + newReq }
        _codeInput.value = ""
    }

    fun acceptRequest(req: FriendRequest) {
        _requests.update { it.filterNot { r -> r.id == req.id } }
        _friends.update { it + Friend(req.id, req.name) }
    }

    fun rejectRequest(req: FriendRequest) {
        _requests.update { it.filterNot { r -> r.id == req.id } }
    }
}
