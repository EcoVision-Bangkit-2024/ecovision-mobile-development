package com.bangkit.ecovision.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.ecovision.data.api.ApiConfig
import com.bangkit.ecovision.data.response.get.Data
import com.bangkit.ecovision.data.response.get.WasteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _materialList = MutableLiveData<List<Data>>()
    val materialList: LiveData<List<Data>> get() = _materialList

    private val _totalMasuk = MutableLiveData<Float>()
    val totalMasuk: LiveData<Float> get() = _totalMasuk

    private val _totalKeluar = MutableLiveData<Float>()
    val totalKeluar: LiveData<Float> get() = _totalKeluar

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val apiService = ApiConfig.getApiService()

    fun fetchMaterials() {
        _isLoading.value = true
        apiService.getWastes().enqueue(object : Callback<WasteResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<WasteResponse>, response: Response<WasteResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val materials = response.body()?.data ?: emptyList()
                    _materialList.value = processMaterials(materials)
                    _totalMasuk.value = calculateSumMasuk(materials)
                    _totalKeluar.value = calculateSumKeluar(materials)
                } else {
                    _materialList.value = emptyList()
                }
            }

            override fun onFailure(call: Call<WasteResponse>, t: Throwable) {
                _isLoading.value = false
                _materialList.value = emptyList()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processMaterials(materials: List<Data>): List<Data> {
        val materialMap = mutableMapOf<String, Int>()

        for (material in materials) {
            val name = material.materialName
            val amount = material.amount
            materialMap[name] = materialMap.getOrDefault(name, 0) + amount
        }

        return materialMap.map { (name, amount) ->
            Data(
                materialName = name,
                amount = amount,
                id = 0,
                date = "",
                keterangan = "",
                purpose = "",
                photoUrl = "",
                type = ""
            )
        }
    }

    private fun calculateSumMasuk(materials: List<Data>): Float {
        var totalMasuk = 0f
        materials.forEach { material ->
            if (material.keterangan == "Waste In") {
                totalMasuk += material.amount?.toFloat() ?: 0f
            }
        }
        return totalMasuk
    }

    private fun calculateSumKeluar(materials: List<Data>): Float {
        var totalKeluar = 0f
        materials.forEach { material ->
            if (material.keterangan == "Waste Out") {
                totalKeluar += material.amount?.toFloat() ?: 0f
            }
        }
        return totalKeluar
    }
}