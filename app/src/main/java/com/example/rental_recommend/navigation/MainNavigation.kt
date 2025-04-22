package com.example.rental_recommend.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.screens.*
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavHostController,
    initialRoute: String = "home"
) {
    val context = LocalContext.current
    
    // 获取当前路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = navBackStackEntry?.destination?.route ?: initialRoute
    
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
                    listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Favorites,
                        BottomNavItem.Profile
                    ).forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRouteName == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("auth") {
                AuthScreen(
                    onNavigateToHome = { navController.navigate("home") }
                )
            }
            composable("home") {
                HomeScreen(
                    onNavigateToFavorites = {
                        navController.navigate("favorites")
                    },
                    onNavigateToProfile = {
                        navController.navigate("profile")
                    },
                    onNavigateToDetail = { id ->
                        navController.navigate("detail/$id")
                    },
                    onNavigateToAuth = { navController.navigate("auth") }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate("detail/$id")
                    },
                    onNavigateToAuth = { navController.navigate("auth") }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        // 清除用户信息
                        UserManager.clearUserInfo(context)
                        // 导航到登录页面
                        navController.navigate("auth") {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = "detail/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                RentalDetailScreen(
                    id = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAuth = { navController.navigate("auth") }
                )
            }
        }
    }
} 