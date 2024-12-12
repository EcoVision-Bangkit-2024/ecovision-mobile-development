package com.bangkit.ecovision.data.repository

import com.bangkit.ecovision.data.api.ApiService
import com.bangkit.ecovision.data.response.add.AddWasteResponse
import com.bangkit.ecovision.data.response.get.WasteResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class WasteRepository(private val apiService: ApiService) {

    fun createWasteWithImage(
        keterangan: String,
        date: String,
        materialName: String,
        purpose: String,
        type: String,
        amount: Int,
        imageFile: File?,
        onResult: (Boolean, String) -> Unit
    ) {
        val keteranganPart = RequestBody.create("text/plain".toMediaTypeOrNull(), keterangan)
        val datePart = RequestBody.create("text/plain".toMediaTypeOrNull(), date)
        val materialNamePart = RequestBody.create("text/plain".toMediaTypeOrNull(), materialName)
        val purposePart = RequestBody.create("text/plain".toMediaTypeOrNull(), purpose)
        val typePart = RequestBody.create("text/plain".toMediaTypeOrNull(), type)
        val amountPart = RequestBody.create("text/plain".toMediaTypeOrNull(), amount.toString())

        val imagePart = imageFile?.let {
            val requestBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", it.name, requestBody)
        }

        apiService.createWasteWithImage(
            keteranganPart,
            datePart,
            materialNamePart,
            purposePart,
            typePart,
            amountPart,
            imagePart
        ).enqueue(object : Callback<AddWasteResponse> {
            override fun onResponse(call: Call<AddWasteResponse>, response:
            Response<AddWasteResponse>) {
                if (response.isSuccessful) {
                    onResult(true, response.body()?.message ?: "Data berhasil dikirim!")
                } else {
                    onResult(false, "Gagal mengirim data. Kode: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AddWasteResponse>, t: Throwable) {
                onResult(false, "Terjadi kesalahan: ${t.message}")
            }
        })
    }

    fun getWastes(onResult: (Boolean, String, WasteResponse?) -> Unit) {
        apiService.getWastes().enqueue(object : Callback<WasteResponse> {
            override fun onResponse(call: Call<WasteResponse>, response: Response<WasteResponse>) {
                if (response.isSuccessful) {
                    onResult(true, "Data berhasil diambil", response.body())
                } else {
                    onResult(false, "Gagal mengambil data. Kode: ${response.code()}", null)
                }
            }

            override fun onFailure(call: Call<WasteResponse>, t: Throwable) {
                onResult(false, "Terjadi kesalahan: ${t.message}", null)
            }
        })
    }

}
