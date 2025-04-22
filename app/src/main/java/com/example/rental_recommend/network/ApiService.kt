package com.example.rental_recommend.network

import com.example.rental_recommend.model.RentalHouse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("user")
    suspend fun getUserInfo(
        @Header("access-token") token: String
    ): Response<UserResponse>

    @PUT("user/profile")
    suspend fun updateUserProfile(
        @Header("access-token") token: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @GET("rental/")
    suspend fun getRentalList(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 10,
        @Query("orderby") orderBy: String = "-id"
    ): Response<RentalListResponse>

    @GET("rental/detail/")
    suspend fun getRentalDetail(
        @Query("id") id: Int
    ): Response<RentalDetailResponse>

    @GET("rental/recommmand/")
    suspend fun getRecommendations(
        @Header("access-token") token: String
    ): Response<List<RentalDetailResponse>>

    @POST("rental/favorite/toggle/")
    suspend fun toggleFavorite(
        @Header("access-token") token: String,
        @Body request: FavoriteRequest
    ): Response<FavoriteResponse>

    @GET("rental/favorite/list/")
    suspend fun getFavorites(
        @Header("access-token") token: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 10
    ): Response<RentalListResponse>

    @GET("rental/favorite/check/")
    suspend fun checkFavorite(
        @Header("access-token") token: String,
        @Query("id") id: Int
    ): Response<FavoriteResponse>

    @GET("rental/search/")
    suspend fun searchRental(
        @Query("query") query: String,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("min_area") minArea: Double? = null,
        @Query("max_area") maxArea: Double? = null,
        @Query("type") type: String? = null,
        @Query("orientation") orientation: String? = null,
        @Query("province") province: String? = null,
        @Query("city") city: String? = null
    ): Response<SearchResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val code: Int,
    val message: String,
    val data: Int? // 用户ID
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val password2: String
)

data class RegisterResponse(
    val code: Int,
    val message: String,
    val data: Int? // 用户ID
)

data class UserResponse(
    val code: Int,
    val message: String,
    val data: UserData?
)

data class UserData(
    val name: String,
    val nickname: String?,
    val email: String?,
    val phone: String?,
    val gender: String?,
    val role: List<String>,
    val isSuperuser: Boolean,
    val budgetMin: Double?,
    val budgetMax: Double?,
    val preferredAreas: List<String>?,
    val houseType: String?
)

data class UpdateProfileRequest(
    val nickname: String?,
    val email: String?,
    val phone: String?,
    val gender: String?,
    val budgetMin: Double?,
    val budgetMax: Double?,
    val preferredAreas: List<String>?,
    val houseType: String?
)

data class UpdateProfileResponse(
    val code: Int,
    val message: String,
    val data: UserData?
)

data class RentalListResponse(
    val total: Int,
    val result: List<RentalHouse>
)

data class RentalDetailResponse(
    val id: Int,
    val cover: String,
    val type: String,
    val title: String,
    val url: String,
    val location: String,
    val areaText: String,
    val area: Double,
    val orientation: String,
    val structure: String,
    val priceText: String,
    val price: Double,
    val tags: String,
    val level: String?,
    val floor: Int?,
    val province: String,
    val city: String,
    val imgs: String,
    val detail: String
)

data class FavoriteRequest(
    val id: Int
)

data class FavoriteResponse(
    val code: Int,
    val message: String,
    val data: FavoriteData?
)

data class FavoriteData(
    val isFavorite: Boolean
)

data class SearchResponse(
    val code: Int,
    val message: String,
    val data: List<RentalHouse>
) 