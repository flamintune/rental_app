package com.example.rental_recommend.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rental_recommend.data.UserManager
import com.example.rental_recommend.network.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: Int) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(username, password)
                .onSuccess { userId ->
                    _authState.value = AuthState.Success(userId)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "登录失败")
                }
        }
    }

    fun register(username: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("两次输入的密码不一致")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.register(username, password, confirmPassword)
                .onSuccess { userId ->
                    _authState.value = AuthState.Success(userId)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "注册失败")
                }
        }
    }

    fun saveUserInfo(context: Context, userId: Int, username: String) {
        UserManager.saveUserInfo(context, userId, username, userId.toString())
    }

    fun clearUserInfo(context: Context) {
        UserManager.clearUserInfo(context)
    }
} 