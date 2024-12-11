package com.bangkit.ecovision.data.response.add


import com.google.gson.annotations.SerializedName

data class AddWasteResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)