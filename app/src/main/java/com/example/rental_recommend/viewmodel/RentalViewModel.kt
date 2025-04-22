package com.example.rental_recommend.viewmodel
import android.util.Log

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.network.RentalRepository
import com.example.rental_recommend.data.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow

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

sealed class RentalDetailState {
    object Loading : RentalDetailState()
    data class Success(val rental: RentalHouse) : RentalDetailState()
    data class Error(val message: String) : RentalDetailState()
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

    private val _detailState = MutableStateFlow<RentalDetailState>(RentalDetailState.Loading)
    val detailState: StateFlow<RentalDetailState> = _detailState.asStateFlow()

    private val _favorites = MutableStateFlow<List<RentalHouse>>(emptyList())
    val favorites: StateFlow<List<RentalHouse>> = _favorites.asStateFlow()

    var lastCheckedRentalId: Int? = null
        private set
        
    var lastOperationWasToggle: Boolean = false
        private set

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
            lastCheckedRentalId = rentalId
            lastOperationWasToggle = true
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
            lastCheckedRentalId = rentalId
            lastOperationWasToggle = false
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

    fun searchRental(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minArea: Double? = null,
        maxArea: Double? = null,
        type: String? = null,
        orientation: String? = null,
        province: String? = null,
        city: String? = null
    ) {
        viewModelScope.launch {
            _rentalListState.value = RentalListState.Loading
            repository.searchRental(
                query, minPrice, maxPrice, minArea, maxArea,
                type, orientation, province, city
            )
                .onSuccess { rentals ->
                    Log.d("RentalViewModel", "searchRental success: ${rentals.size} results")
                    _rentalListState.value = RentalListState.Success(rentals)
                }
                .onFailure { error ->
                    Log.e("RentalViewModel", "searchRental error: ${error.message}")
                    _rentalListState.value = RentalListState.Error(error.message ?: "搜索失败")
                }
        }
    }

    fun getRentalDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = RentalDetailState.Loading
            val token = UserManager.getToken(context) ?: run {
                _events.value = RentalEvent.AuthError
                return@launch
            }
            Log.d("RentalViewModel", "getRentalDetail: 开始获取房源详情, id=$id")
            repository.getRentalDetail(token, id)
                .onSuccess { rental ->
                    Log.d("RentalViewModel", "getRentalDetail success: ${rental}")
                    _detailState.value = RentalDetailState.Success(rental)
                }
                .onFailure { exception ->
                    Log.e("RentalViewModel", "getRentalDetail error: ${exception.message}", exception)
                    _detailState.value = RentalDetailState.Error(exception.message ?: "获取房屋详情失败")
                }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _rentalListState.value = RentalListState.Loading
            val token = UserManager.getToken(context) ?: run {
                _events.value = RentalEvent.AuthError
                return@launch
            }
            try {
                val result = repository.getFavorites(token)
                result.onSuccess { rentals ->
                    _favorites.value = rentals ?: emptyList()
                    _rentalListState.value = RentalListState.Success(rentals ?: emptyList())
                }.onFailure { error ->
                    _rentalListState.value = RentalListState.Error(error.message ?: "加载收藏列表失败")
                }
            } catch (e: Exception) {
                _rentalListState.value = RentalListState.Error(e.message ?: "加载收藏列表失败")
            }
        }
    }
} 