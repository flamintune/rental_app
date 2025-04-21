package com.example.rental_recommend.model

import com.google.gson.annotations.SerializedName

data class RentalHouse(
    val id: Int,
    val cover: String,
    val type: String,
    val title: String,
    val url: String,
    val location: String,
    @SerializedName("area_text")
    val areaText: String,
    val area: Double,
    val orientation: String,
    val structure: String,
    @SerializedName("price_text")
    val priceText: String,
    val price: Double,
    val tags: String,
    val level: String?,
    val floor: Int?,
    val province: String,
    val city: String,
    val imgs: String,
    val detail: String
) 