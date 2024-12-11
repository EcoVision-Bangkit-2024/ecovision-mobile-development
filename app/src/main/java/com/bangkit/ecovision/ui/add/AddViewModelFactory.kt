package com.bangkit.ecovision.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.ecovision.data.repository.WasteRepository

class AddViewModelFactory(private val repository: WasteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddViewModel(repository) as T
    }
}