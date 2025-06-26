package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appranzo.communication.remote.RestApiClient


class SearchViewModel(
    private val restApiClient: RestApiClient
) : ViewModel() {
}
