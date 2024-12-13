package com.bangkit.ecovision.ui.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedType = MutableLiveData<String?>()
    val selectedType: LiveData<String?> get() = _selectedType

    fun setSelectedType(type: String?) {
        _selectedType.value = type
    }
}