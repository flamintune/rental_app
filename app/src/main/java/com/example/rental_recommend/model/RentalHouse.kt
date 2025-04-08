package com.example.rental_recommend.model

data class RentalHouse(
    val id: String,
    val title: String,
    val price: Int,
    val location: String,
    val area: Float,
    val rooms: Int,
    val imageUrl: String,
    val tags: List<String>,
    val description: String,
    val isFavorite: Boolean = false
) 