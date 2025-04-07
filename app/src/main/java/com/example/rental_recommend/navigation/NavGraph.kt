package com.example.rental_recommend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rental_recommend.screens.HouseDetailScreen
import com.example.rental_recommend.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Profile.route
    ) {
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToFavorite = {
                    navController.navigate(Screen.Favorite.route)
                }
            )
        }
        composable(
            route = "${Screen.HouseDetail.route}/{houseId}",
            arguments = listOf(
                navArgument("houseId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val houseId = backStackEntry.arguments?.getInt("houseId") ?: 0
            HouseDetailScreen(
                houseId = houseId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Profile : Screen("profile")
    object Favorite : Screen("favorite")
    object HouseDetail : Screen("house_detail")
} 