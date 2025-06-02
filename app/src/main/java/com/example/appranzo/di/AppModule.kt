@file:Suppress("DEPRECATION")

package com.example.appranzo.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.data.repository.TokensRepository
import com.example.appranzo.viewmodel.AuthViewModel
import com.example.appranzo.viewmodel.BadgeRoadViewModel
import com.example.appranzo.viewmodel.FriendsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
val Context.dataStore by preferencesDataStore("tokens")


val appModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
        }
    }
    single{RestApiClient(get())}
    single { get<Context>().dataStore }
    single { TokensRepository(get()) }
    viewModel { AuthViewModel(get(),get()) }
    viewModel { BadgeRoadViewModel() }
    viewModel { FriendsViewModel() }
}
