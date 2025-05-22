@file:Suppress("DEPRECATION")

package com.example.appranzo.di

import com.example.appranzo.viewmodel.AuthViewModel
import com.example.appranzo.viewmodel.BadgeRoadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AuthViewModel() }
    viewModel { BadgeRoadViewModel() }
}
