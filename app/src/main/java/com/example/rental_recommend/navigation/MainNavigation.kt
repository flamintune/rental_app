package com.example.rental_recommend.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rental_recommend.data.MockData
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("auth") }
    var houses by remember { mutableStateOf(MockData.rentalHouses) }
    
    // 获取当前路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = navBackStackEntry?.destination?.route ?: "auth"
    
    // 判断是否显示底部导航栏
    val showBottomBar = when {
        currentRouteName == "auth" -> false
        currentRouteName.startsWith("detail/") -> false
        currentRouteName == "home" -> true
        currentRouteName == "favorites" -> true
        currentRouteName == "profile" -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                AuthScreen(
                    onLoginSuccess = {
                        currentRoute = "home"
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        currentRoute = "home"
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            
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
                    onNavigateToDetail = { house: RentalHouse ->
                        navController.navigate("detail/${house.id}")
                    },
                    houses = houses,
                    onHousesChange = { houses = it }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onNavigateToDetail = { house: RentalHouse ->
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