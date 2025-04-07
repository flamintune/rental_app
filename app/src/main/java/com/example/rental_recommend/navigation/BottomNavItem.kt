package com.example.rental_recommend.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "首页",
        icon = Icons.Default.Home
    )
    
    object Search : BottomNavItem(
        route = "search",
        title = "搜索",
        icon = Icons.Default.Search
    )
    
    object Favorites : BottomNavItem(
        route = "favorites",
        title = "收藏",
        icon = Icons.Default.Favorite
    )
    
    object Profile : BottomNavItem(
        route = "profile",
        title = "我的",
        icon = Icons.Default.Person
    )
} 