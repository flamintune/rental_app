package com.example.rental_recommend.network

import com.example.rental_recommend.model.RentalHouse
import retrofit2.Response

class RentalRepository(private val apiService: ApiService) {
    
    suspend fun getRentalList(page: Int = 1, pageSize: Int = 10): Result<List<RentalHouse>> {
        return try {
            val response = apiService.getRentalList(page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.result)
            } else {
                Result.failure(Exception("获取房源列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRentalDetail(id: Int): Result<RentalHouse> {
        return try {
            val response = apiService.getRentalDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toRentalHouse())
            } else {
                Result.failure(Exception("获取房源详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecommendations(token: String): Result<List<RentalHouse>> {
        return try {
            val response = apiService.getRecommendations(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toRentalHouse() })
            } else {
                Result.failure(Exception("获取推荐房源失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(token: String, rentalId: Int): Result<Boolean> {
        return try {
            val response = apiService.toggleFavorite(token, FavoriteRequest(rentalId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data?.isFavorite ?: false)
            } else {
                Result.failure(Exception("操作失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(token: String, page: Int = 1, pageSize: Int = 10): Result<List<RentalHouse>> {
        return try {
            val response = apiService.getFavorites(token, page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.result)
            } else {
                Result.failure(Exception("获取收藏列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkFavorite(token: String, rentalId: Int): Result<Boolean> {
        return try {
            val response = apiService.checkFavorite(token, rentalId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data?.isFavorite ?: false)
            } else {
                Result.failure(Exception("检查收藏状态失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchRental(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minArea: Double? = null,
        maxArea: Double? = null,
        type: String? = null,
        orientation: String? = null,
        province: String? = null,
        city: String? = null
    ): Result<List<RentalHouse>> {
        return try {
            val response = apiService.searchRental(
                query, minPrice, maxPrice, minArea, maxArea,
                type, orientation, province, city
            )
            if (response.isSuccessful && response.body() != null && response.body()!!.code == 200) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.body()?.message ?: "搜索房源失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun RentalDetailResponse.toRentalHouse(): RentalHouse {
        return RentalHouse(
            id = id,
            cover = cover,
            type = type,
            title = title,
            url = url,
            location = location,
            areaText = areaText,
            area = area,
            orientation = orientation,
            structure = structure,
            priceText = priceText,
            price = price,
            tags = tags,
            level = level,
            floor = floor,
            province = province,
            city = city,
            imgs = imgs,
        detail = detail
        )
    }
} 