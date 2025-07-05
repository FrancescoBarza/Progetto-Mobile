package com.example.appranzo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BadgeRoadViewModel : ViewModel() {
    private val thresholds = (1..10).map { it * 100 }

    private val _currentPoints = MutableStateFlow(0)
    val currentPoints: StateFlow<Int> = _currentPoints

    val badgeThresholds: List<Int> get() = thresholds

    fun addPoints(amount: Int = 10) {
        _currentPoints.update { (it + amount).coerceAtMost(thresholds.last()) }
    }
}
