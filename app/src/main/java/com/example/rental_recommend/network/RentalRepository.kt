package com.example.rental_recommend.network

import android.util.Log
import com.example.rental_recommend.model.RentalHouse
import retrofit2.Response

class RentalRepository(private val apiService: ApiService) {
    
    suspend fun getRentalList(page: Int = 1, pageSize: Int = 10): Result<List<RentalHouse>> {
        return try {
            val response = apiService.getRentalList(page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    Result.success(body.data.result)
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception("获取房源列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRentalDetail(token: String, id: Int): Result<RentalHouse> {
        return try {
            val response = apiService.getRentalDetail(token, id)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    Result.success(body.data.toRentalHouse())
                } else {
                    Result.failure(Exception(body.message))
                }
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
                val body = response.body()!!
                if (body.code == 200) {
                    body.data?.let { data ->
                        Result.success(data.map { it.toRentalHouse() })
                    } ?: Result.success(emptyList())
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception("获取推荐房源失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(token: String, rentalId: Int): Result<Boolean> {
        return try {
            val response = apiService.toggleFavorite(
                token = token,
                request = mapOf("id" to rentalId)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    body.data?.get("isFavorite")?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("操作失败：返回数据格式错误"))
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(token: String, page: Int = 1, pageSize: Int = 10): Result<List<RentalHouse>> {
        return try {
            val response = apiService.getFavorites(
                token = token,
                page = page,
                pageSize = pageSize
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    Result.success(body.data.result)
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception("获取收藏列表失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkFavorite(token: String, rentalId: Int): Result<Boolean> {
        return try {
            val response = apiService.checkFavorite(
                token = token,
                id = rentalId
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    body.data?.get("isFavorite")?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("检查收藏状态失败：返回数据格式错误"))
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception(response.message()))
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
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.code == 200) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body.message))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun RentalDetailData.toRentalHouse(): RentalHouse {
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
            tags = tags ?: "",
            level = level,
            floor = floor,
            province = province,
            city = city,
            imgs = imgs ?: "",
            detail = detail ?: ""
        )
    }
} 