package com.bangkit.ecovision.ui.analytics

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.ecovision.R
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.data.response.get.Data
import java.io.BufferedReader
import java.io.InputStreamReader

class AnalyticsViewModel(private val wasteRepository: WasteRepository) : ViewModel() {

    private val _filteredWasteData = MutableLiveData<List<Data>>()
    val filteredWasteData: LiveData<List<Data>> = _filteredWasteData

    private val _base64Image = MutableLiveData<String>()
    val base64Image: LiveData<String> = _base64Image

    fun loadBase64ImageFromFile(context: Context) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.image64)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val base64String = reader.readLine()
            _base64Image.value = base64String
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun loadWasteDataByType(type: String) {
        wasteRepository.getWastes { success, message, response ->
            if (success) {
                // Filter data berdasarkan type
                val filteredData = response?.data?.filter { it.type == type } ?: emptyList()
                _filteredWasteData.value = filteredData
            } else {
                // Handle error
                Log.e("AnalyticsViewModel", "Error: $message")
            }
        }
    }
}