package com.bangkit.ecovision.data.response.get


import com.google.gson.annotations.SerializedName

data class WasteResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)