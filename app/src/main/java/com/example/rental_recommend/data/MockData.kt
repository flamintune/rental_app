package com.example.rental_recommend.data

import com.example.rental_recommend.model.RentalHouse

object MockData {
    val rentalHouses = listOf(
        RentalHouse(
            id = "1",
            title = "阳光花园精装两房",
            price = 3500,
            location = "海淀区",
            area = 89.5f,
            rooms = 2,
            imageUrl = "https://picsum.photos/400/300",
            tags = listOf("精装修", "近地铁", "随时看房"),
            description = "房屋采光好，家具家电齐全，拎包入住",
            isFavorite = false
        ),
        RentalHouse(
            id = "2",
            title = "望京SOHO附近三房",
            price = 6800,
            location = "朝阳区",
            area = 120f,
            rooms = 3,
            imageUrl = "https://picsum.photos/400/301",
            tags = listOf("地铁房", "品牌公寓", "有电梯"),
            description = "望京SOHO附近，交通便利，周边配套齐全",
            isFavorite = true
        ),
        RentalHouse(
            id = "3",
            title = "西二旗地铁站旁一房",
            price = 2800,
            location = "海淀区",
            area = 45f,
            rooms = 1,
            imageUrl = "https://picsum.photos/400/302",
            tags = listOf("地铁房", "单身公寓", "精装修"),
            description = "西二旗地铁站步行5分钟，适合上班族",
            isFavorite = false
        )
    )
} 