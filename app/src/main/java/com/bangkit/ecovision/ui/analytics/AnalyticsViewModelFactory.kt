package com.bangkit.ecovision.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.data.repository.WasteRepository

class AnalyticsViewModelFactory(private val wasteRepository: WasteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalyticsViewModel(wasteRepository) as T
    }
}

