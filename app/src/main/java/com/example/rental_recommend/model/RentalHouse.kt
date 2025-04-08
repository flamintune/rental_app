package com.example.rental_recommend.model

data class RentalHouse(
    val id: Int,
    val title: String,
    val price: Int,
    val location: String,
    val area: Float,
    val rooms: Int,
    val imageUrl: String,
    val tags: List<String>,
    val description: String,
    val orientation: String = "南北通透",
    val isFavorite: Boolean = false,
    val unitPrice: Float = 0f,  // 单价
    val listingDate: String = "", // 挂牌时间
    val floor: String = "",      // 楼层信息
    val buildingType: String = "", // 楼型
    val serviceStandards: List<String> = emptyList(), // 服务标准
    val riskTips: List<String> = emptyList()  // 风险提示
) 