package com.bangkit.ecovision.data.model

data class WasteModel(
    val status: String,
    val date: String,
    val materialName: String,
    val type: String,
    val amount: Int,
    val photo: String?
)
