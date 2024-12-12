package com.bangkit.ecovision.ui.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // LiveData untuk menyimpan tipe yang dipilih
    private val _selectedType = MutableLiveData<String>()
    val selectedType: LiveData<String> get() = _selectedType

    // Fungsi untuk mengubah tipe yang dipilih
    fun setSelectedType(type: String) {
        _selectedType.value = type
    }
}