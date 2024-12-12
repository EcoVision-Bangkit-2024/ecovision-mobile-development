package com.bangkit.ecovision.ui.analytics

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.ecovision.R
import com.bangkit.ecovision.data.repository.WasteRepository
import com.bangkit.ecovision.data.response.get.Data
import com.github.mikephil.charting.data.BarEntry
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.lifecycle.MediatorLiveData


class AnalyticsViewModel(private val wasteRepository: WasteRepository) : ViewModel() {

    private val _base64Image = MutableLiveData<String>()
    val base64Image: LiveData<String> = _base64Image

    private val _base64AnorganicImage = MutableLiveData<String>()
    val base64AnorganicImage: LiveData<String> = _base64AnorganicImage

    private val _chartDataMasuk = MutableLiveData<List<BarEntry>>()
    val chartDataMasuk: LiveData<List<BarEntry>> = _chartDataMasuk

    private val _chartDataKeluar = MutableLiveData<List<BarEntry>>()
    val chartDataKeluar: LiveData<List<BarEntry>> = _chartDataKeluar

    private val _materialNamesMasuk = MutableLiveData<List<String>>()
    val materialNamesMasuk: LiveData<List<String>> = _materialNamesMasuk

    private val _materialNamesKeluar = MutableLiveData<List<String>>()
    val materialNamesKeluar: LiveData<List<String>> = _materialNamesKeluar

    private val _sumMasuk = MutableLiveData<Float>()
    val sumMasuk: LiveData<Float> = _sumMasuk

    private val _sumKeluar = MutableLiveData<Float>()
    val sumKeluar: LiveData<Float> = _sumKeluar

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

    fun loadBase64AnorganicImageFromFile(context: Context) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.anorganic)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val base64String = reader.readLine()
            _base64AnorganicImage.value = base64String
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadWasteData(selectedType: String?) {
        wasteRepository.getWastes { success, _, response ->
            if (success && response != null) {
                val filteredData = response.data.filter { it.type == selectedType }

                val dataMasuk = filteredData.filter { it.keterangan == "Masuk" && it.amount > 0 }
                val dataKeluar = filteredData.filter { it.keterangan == "Keluar" && it.amount > 0 }

                // Grouping data and summing amounts using forEach
                val groupedMasuk = dataMasuk.groupBy { it.materialName }.mapValues { entry ->
                    var sum = 0f
                    entry.value.forEach { item ->
                        sum += item.amount?.toFloat() ?: 0f
                    }
                    sum
                }

                val groupedKeluar = dataKeluar.groupBy { it.materialName }.mapValues { entry ->
                    var sum = 0f
                    entry.value.forEach { item ->
                        sum += item.amount?.toFloat() ?: 0f
                    }
                    sum
                }

                var sumAmount = 0f
                dataMasuk.forEach {
                    sumAmount += it.amount?.toFloat() ?: 0f
                }
                _sumMasuk.value = sumAmount

                var sumAmountKeluar = 0f
                dataKeluar.forEach {
                    sumAmountKeluar += it.amount?.toFloat() ?: 0f
                }
                _sumKeluar.value = sumAmountKeluar

                val chartDataMasuk = groupedMasuk.map { (key, value) ->
                    BarEntry(groupedMasuk.keys.indexOf(key).toFloat(), value)
                }
                val materialNamesMasuk = groupedMasuk.keys.toList()

                val chartDataKeluar = groupedKeluar.map {  (key, value) ->
                    BarEntry(groupedKeluar.keys.indexOf(key).toFloat(), value)
                }
                val materialNamesKeluar = groupedKeluar.keys.toList()

                _chartDataMasuk.value = chartDataMasuk
                _chartDataKeluar.value = chartDataKeluar
                _materialNamesMasuk.value = materialNamesMasuk
                _materialNamesKeluar.value = materialNamesKeluar
            }
        }
    }
}