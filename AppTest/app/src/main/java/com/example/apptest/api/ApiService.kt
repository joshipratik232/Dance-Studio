package com.example.apptest.api

import com.example.apptest.model.categoryHome.CategoryResponse
import com.example.apptest.model.categorySeeAll.CategoryWiseVideoResponse
import com.example.apptest.model.image.VideoResponse
import com.example.apptest.model.login.LoginResponse
import com.example.apptest.model.notify.NotifyResponse
import com.example.apptest.model.recentVideo.RecentVideo
import com.example.apptest.model.videoDetail.AddToList
import com.example.apptest.model.videoDetail.LikeChange
import com.example.apptest.model.videoDetail.VideoDetailResponse
import com.google.gson.JsonObject
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

private const val BASE_URL = "http://services.accgo.net"
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

interface ApiService {
    @POST("/DS/API/Authentication")
    fun loginAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap header: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<LoginResponse>

    @GET("/DS/API/VideoLibrary")
    fun getVideoAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap token: Map<String?, String?>,
            @HeaderMap header: Map<String?, String?>
    ): Deferred<VideoResponse>

    @GET("/DS/API/RecentVideo")
    fun getRecentVideoAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>
    ): Deferred<RecentVideo>

    @POST("DS/API/GetCategoryWithVideos")
    fun categoryVideoAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<CategoryResponse>

    @POST("DS/API/GetCategoryBaseAllVideos")
    fun catWiseVideoAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<CategoryWiseVideoResponse>

    @POST("DS/API/GetNotificationList")
    fun notifyListAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<NotifyResponse>

    @POST("DS/API/GetVideoDetails")
    fun videoDetailAsync(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<VideoDetailResponse>

    @POST("DS/API/LikeChange")
    fun likeChange(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<LikeChange>

    @POST("DS/API/AddToMyList")
    fun addToList(
            @HeaderMap clientKey: Map<String?, String?>,
            @HeaderMap contentType: Map<String?, String?>,
            @HeaderMap authorization: Map<String?, String?>,
            @Body body: JsonObject
    ): Deferred<AddToList>
}

object Api {
    val RETROFIT_SERVICE: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}