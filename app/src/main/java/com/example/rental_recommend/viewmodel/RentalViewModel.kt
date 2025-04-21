package com.example.rental_recommend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.network.RentalRepository
import com.example.rental_recommend.data.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RentalListState {
    object Loading : RentalListState()
    data class Success(val rentals: List<RentalHouse>) : RentalListState()
    data class Error(val message: String) : RentalListState()
}

sealed class FavoriteState {
    object Loading : FavoriteState()
    data class Success(val isFavorite: Boolean) : FavoriteState()
    data class Error(val message: String) : FavoriteState()
}

sealed class RentalEvent {
    object AuthError : RentalEvent()
}

class RentalViewModel(
    private val repository: RentalRepository,
    private val context: Context
) : ViewModel() {
    private val _rentalListState = MutableStateFlow<RentalListState>(RentalListState.Loading)
    val rentalListState: StateFlow<RentalListState> = _rentalListState

    private val _favoriteState = MutableStateFlow<FavoriteState>(FavoriteState.Loading)
    val favoriteState: StateFlow<FavoriteState> = _favoriteState

    private val _events = MutableStateFlow<RentalEvent?>(null)
    val events: StateFlow<RentalEvent?> = _events

    private var currentPage = 1
    private val pageSize = 10

    fun loadRentalList() {
        viewModelScope.launch {
            _rentalListState.value = RentalListState.Loading
            repository.getRentalList(currentPage, pageSize)
                .onSuccess { rentals ->
                    _rentalListState.value = RentalListState.Success(rentals)
                }
                .onFailure { error ->
                    _rentalListState.value = RentalListState.Error(error.message ?: "加载失败")
                }
        }
    }

    fun loadMore() {
        if (_rentalListState.value !is RentalListState.Success) return
        
        viewModelScope.launch {
            currentPage++
            repository.getRentalList(currentPage, pageSize)
                .onSuccess { newRentals ->
                    val currentRentals = (_rentalListState.value as RentalListState.Success).rentals
                    _rentalListState.value = RentalListState.Success(currentRentals + newRentals)
                }
                .onFailure { error ->
                    _rentalListState.value = RentalListState.Error(error.message ?: "加载更多失败")
                }
        }
    }

    fun refresh() {
        currentPage = 1
        loadRentalList()
    }

    private fun handleAuthError() {
        val userManager = UserManager.getInstance()
        userManager.clearUserInfo(context)
        _events.value = RentalEvent.AuthError
    }

    fun toggleFavorite(rentalId: Int) {
        viewModelScope.launch {
            _favoriteState.value = FavoriteState.Loading
            val token = UserManager.getToken(context) ?: run {
                _events.value = RentalEvent.AuthError
                return@launch
            }
            repository.toggleFavorite(token, rentalId)
                .onSuccess { isFavorite ->
                    _favoriteState.value = FavoriteState.Success(isFavorite)
                }
                .onFailure { error ->
                    _favoriteState.value = FavoriteState.Error(error.message ?: "操作失败")
                }
        }
    }

    fun checkFavorite(rentalId: Int) {
        viewModelScope.launch {
            _favoriteState.value = FavoriteState.Loading
            val token = UserManager.getToken(context) ?: run {
                _events.value = RentalEvent.AuthError
                return@launch
            }
            repository.checkFavorite(token, rentalId)
                .onSuccess { isFavorite ->
                    _favoriteState.value = FavoriteState.Success(isFavorite)
                }
                .onFailure { error ->
                    _favoriteState.value = FavoriteState.Error(error.message ?: "检查收藏状态失败")
                }
        }
    }

    fun clearEvent() {
        _events.value = null
    }
} 