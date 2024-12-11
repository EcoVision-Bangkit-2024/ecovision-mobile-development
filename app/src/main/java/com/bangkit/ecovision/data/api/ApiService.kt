package com.bangkit.ecovision.data.api

import com.bangkit.ecovision.data.response.add.AddWasteResponse
import com.bangkit.ecovision.data.response.get.WasteResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/wastes")
    fun createWasteWithImage(
        @Part("keterangan") keterangan: RequestBody,
        @Part("date") date: RequestBody,
        @Part("materialName") materialName: RequestBody,
        @Part("type") type: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<AddWasteResponse>

    @GET("api/wastes")
    fun getWastes(): Call<WasteResponse>
}