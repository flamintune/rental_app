package com.example.rental_recommend.network

import com.example.rental_recommend.network.RetrofitClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    suspend fun login(username: String, password: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body()?.code == 200) {
                val userId = response.body()?.data
                if (userId != null) {
                    Result.success(userId)
                } else {
                    Result.failure(Exception("登录失败：用户ID为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "登录失败"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("网络错误：${e.message}"))
        }
    }

    suspend fun register(username: String, password: String, confirmPassword: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(username, password, confirmPassword))
            if (response.isSuccessful && response.body()?.code == 200) {
                val userId = response.body()?.data
                if (userId != null) {
                    Result.success(userId)
                } else {
                    Result.failure(Exception("注册失败：用户ID为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "注册失败"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("网络错误：${e.message}"))
        }
    }

    suspend fun getUserInfo(token: String): Result<UserData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserInfo(token)
            if (response.isSuccessful && response.body()?.code == 200) {
                val userData = response.body()?.data
                if (userData != null) {
                    Result.success(userData)
                } else {
                    Result.failure(Exception("获取用户信息失败：数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "获取用户信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("网络错误：${e.message}"))
        }
    }

    suspend fun updateUserProfile(
        token: String,
        nickname: String?,
        email: String?,
        phone: String?,
        gender: String?,
        budgetMin: Double?,
        budgetMax: Double?,
        preferredAreas: List<String>?,
        houseType: String?
    ): Result<UserData> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(
                nickname = nickname,
                email = email,
                phone = phone,
                gender = gender,
                budgetMin = budgetMin,
                budgetMax = budgetMax,
                preferredAreas = preferredAreas,
                houseType = houseType
            )
            val response = apiService.updateUserProfile(token, request)
            if (response.isSuccessful && response.body()?.code == 200) {
                val userData = response.body()?.data
                if (userData != null) {
                    Result.success(userData)
                } else {
                    Result.failure(Exception("更新用户信息失败：数据为空"))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "更新用户信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("网络错误：${e.message}"))
        }
    }
} 