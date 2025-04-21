package com.example.rental_recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.navigation.MainNavigation
import com.example.rental_recommend.ui.theme.RentalRecommendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 尝试自动登录
        val isLoggedIn = UserManager.loadUserInfo(this)
        
        setContent {
            RentalRecommendTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MainNavigation(navController = navController, initialRoute = if (isLoggedIn) "home" else "auth")
                }
            }
        }
    }
}