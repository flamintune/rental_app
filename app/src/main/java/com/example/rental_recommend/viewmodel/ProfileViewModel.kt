package com.example.rental_recommend.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.model.User
import com.example.rental_recommend.network.AuthRepository
import com.example.rental_recommend.network.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadUserProfile(context: Context) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val token = UserManager.getToken(context)
                if (token == null) {
                    _profileState.value = ProfileState.Error("未登录")
                    return@launch
                }
                
                Log.d("ProfileViewModel", "开始加载用户信息，token: $token")
                val result = authRepository.getUserInfo(token)
                result.fold(
                    onSuccess = { userData ->
                        Log.d("ProfileViewModel", "成功加载用户信息: $userData")
                        _profileState.value = ProfileState.Success(userData.toUser())
                    },
                    onFailure = { error ->
                        Log.e("ProfileViewModel", "加载用户信息失败", error)
                        _profileState.value = ProfileState.Error(error.message ?: "加载用户信息失败")
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "加载用户信息时发生异常", e)
                _profileState.value = ProfileState.Error(e.message ?: "加载用户信息失败")
            }
        }
    }

    fun updateUserProfile(
        context: Context,
        nickname: String? = null,
        email: String? = null,
        phone: String? = null,
        gender: String? = null,
        budgetMin: Double? = null,
        budgetMax: Double? = null,
        preferredAreas: List<String>? = null,
        houseType: String? = null
    ) {
        viewModelScope.launch {
            try {
                val token = UserManager.getToken(context)
                if (token == null) {
                    _profileState.value = ProfileState.Error("未登录")
                    return@launch
                }

                val result = authRepository.updateUserProfile(
                    token = token,
                    nickname = nickname,
                    email = email,
                    phone = phone,
                    gender = gender,
                    budgetMin = budgetMin,
                    budgetMax = budgetMax,
                    preferredAreas = preferredAreas,
                    houseType = houseType
                )

                result.fold(
                    onSuccess = { userData ->
                        _profileState.value = ProfileState.Success(userData.toUser())
                    },
                    onFailure = { error ->
                        _profileState.value = ProfileState.Error(error.message ?: "更新用户信息失败")
                    }
                )
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "更新用户信息失败")
            }
        }
    }
}

private fun UserData.toUser(): User {
    return User(
        username = name,
        nickname = nickname,
        email = email ?: "",
        phone = phone,
        gender = gender ?: "未设置",
        budgetMin = budgetMin,
        budgetMax = budgetMax,
        preferredAreas = preferredAreas ?: emptyList(),
        houseType = houseType,
        isLandlord = role.contains("landlord"),
        isTenant = role.contains("tenant")
    )
} 