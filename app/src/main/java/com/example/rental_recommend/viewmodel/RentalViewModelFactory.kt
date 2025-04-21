package com.example.rental_recommend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rental_recommend.network.RentalRepository
import com.example.rental_recommend.network.RetrofitClient

class RentalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RentalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RentalViewModel(RentalRepository(RetrofitClient.apiService), context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 