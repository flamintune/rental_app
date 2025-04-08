package com.example.rental_recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rental_recommend.data.MockData
import com.example.rental_recommend.screens.*
import com.example.rental_recommend.ui.theme.RentalRecommendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalRecommendTheme {
                val navController = rememberNavController()
                var currentRoute by remember { mutableStateOf("home") }
                var houses by remember { mutableStateOf(MockData.rentalHouses) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                                label = { Text("首页") },
                                selected = currentRoute == "home",
                                onClick = {
                                    currentRoute = "home"
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Favorite, contentDescription = "收藏") },
                                label = { Text("收藏") },
                                selected = currentRoute == "favorites",
                                onClick = {
                                    currentRoute = "favorites"
                                    navController.navigate("favorites") {
                                        popUpTo("favorites") { inclusive = true }
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = "我的") },
                                label = { Text("我的") },
                                selected = currentRoute == "profile",
                                onClick = {
                                    currentRoute = "profile"
                                    navController.navigate("profile") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onNavigateToFavorites = {
                                    currentRoute = "favorites"
                                    navController.navigate("favorites")
                                },
                                onNavigateToProfile = {
                                    currentRoute = "profile"
                                    navController.navigate("profile")
                                },
                                onNavigateToDetail = { house ->
                                    navController.navigate("detail/${house.id}")
                                },
                                houses = houses,
                                onHousesChange = { houses = it }
                            )
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                onNavigateToDetail = { house ->
                                    navController.navigate("detail/${house.id}")
                                },
                                houses = houses,
                                onHousesChange = { houses = it }
                            )
                        }
                        composable("profile") {
                            ProfileScreen()
                        }
                        composable("detail/{houseId}") { backStackEntry ->
                            val houseId = backStackEntry.arguments?.getString("houseId")?.toIntOrNull() ?: 0
                            val house = houses.find { it.id == houseId }
                            if (house != null) {
                                HouseDetailScreen(
                                    house = house,
                                    onBackClick = { navController.popBackStack() },
                                    onFavoriteClick = { updatedHouse ->
                                        // 更新收藏状态
                                        houses = houses.map { 
                                            if (it.id == updatedHouse.id) updatedHouse else it 
                                        }
                                    },
                                    onContactClick = { /* TODO: 实现联系房东功能 */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}