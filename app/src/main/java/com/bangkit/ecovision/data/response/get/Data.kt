package com.bangkit.ecovision.data.response.get


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("keterangan")
    val keterangan: String,
    @SerializedName("materialName")
    val materialName: String,
    @SerializedName("purpose")
    val purpose: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("type")
    val type: String
)