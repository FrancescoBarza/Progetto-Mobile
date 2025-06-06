@file:Suppress("DEPRECATION")

package com.example.appranzo.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.appranzo.communication.remote.RestApiClient
import com.example.appranzo.data.repository.ThemeRepository
import com.example.appranzo.data.repository.TokensRepository
import com.example.appranzo.ui.screens.ThemeViewModel
import com.example.appranzo.viewmodel.AuthViewModel
import com.example.appranzo.viewmodel.BadgeRoadViewModel
import com.example.appranzo.viewmodel.FriendsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
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
    single {
        PreferenceDataStoreFactory.create {
            androidContext().preferencesDataStoreFile("settings")
        }
    }
    single{RestApiClient(get())}
    single { get<Context>().dataStore }
    single { TokensRepository(get()) }
    single { ThemeRepository(dataStore = get()) }

    viewModel { ThemeViewModel(repository = get()) }
    viewModel { AuthViewModel(get(),get()) }
    viewModel { BadgeRoadViewModel() }
    viewModel { FriendsViewModel() }

}
