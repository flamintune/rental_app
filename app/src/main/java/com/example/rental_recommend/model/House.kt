package com.example.rental_recommend.model

data class House(
    val id: Int,
    val cover: String,
    val type: String,
    val title: String,
    val oid: String,
    val url: String,
    val location: String,
    val areaText: String,
    val area: Double,
    val orientation: String,
    val structure: String,
    val priceText: String,
    val price: Double,
    val tags: String,
    val level: String,
    val floor: Int,
    val province: String,
    val city: String,
    val imgs: String,
    val detail: String
) 