package com.example.rental_recommend.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val nickname: String? = null,
    val email: String = "",
    val phone: String? = null,
    val avatar: String? = null,
    val gender: String = "未设置",
    val isVerified: Boolean = false,
    val budgetMin: Double? = null,
    val budgetMax: Double? = null,
    val preferredAreas: List<String> = emptyList(),
    val houseType: String? = null,
    val isLandlord: Boolean = false,
    val isTenant: Boolean = true
) 