package com.example.rental_recommend.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.net.InetAddress
import java.net.UnknownHostException
import okhttp3.Dns

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/" // Android模拟器访问本机服务的地址
    private const val TIMEOUT = 30L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val customDns = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            Log.d("RetrofitClient", "DNS查询: $hostname")
            return try {
                val addresses = InetAddress.getAllByName(hostname)
                Log.d("RetrofitClient", "DNS查询结果: ${addresses.joinToString()}")
                addresses.toList()
            } catch (e: UnknownHostException) {
                Log.e("RetrofitClient", "DNS查询失败: $hostname", e)
                throw e
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            try {
                // 尝试解析域名
                val host = request.url.host
                Log.d("RetrofitClient", "正在解析域名: $host")
                val addresses = InetAddress.getAllByName(host)
                Log.d("RetrofitClient", "域名解析结果: ${addresses.joinToString()}")
            } catch (e: UnknownHostException) {
                Log.e("RetrofitClient", "域名解析失败: ${request.url.host}", e)
            }
            chain.proceed(request)
        }
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .dns(customDns)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
} 