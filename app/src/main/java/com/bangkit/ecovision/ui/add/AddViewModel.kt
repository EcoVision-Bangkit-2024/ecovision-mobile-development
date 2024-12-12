package com.bangkit.ecovision.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.data.model.WasteModel
import java.io.File

class AddViewModel(private val repository: WasteRepository) : ViewModel() {

    private val _submitStatus = MutableLiveData<Pair<Boolean, String>>()
    val submitStatus: LiveData<Pair<Boolean, String>> get() = _submitStatus

    fun submitWasteWithImage(
        keterangan: String,
        date: String,
        materialName: String,
        purpose: String,
        type: String,
        amount: Int,
        imageFile: File?
    ) {
        repository.createWasteWithImage(keterangan, date, materialName, purpose, type, amount, imageFile) { isSuccess, message ->
            _submitStatus.value = Pair(isSuccess, message)
        }
    }
}
