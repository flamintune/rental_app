package com.example.rental_recommend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rental_recommend.navigation.MainNavigation
import com.example.rental_recommend.ui.theme.RentalRecommendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalRecommendTheme {
                MainNavigation()
            }
        }
    }
}