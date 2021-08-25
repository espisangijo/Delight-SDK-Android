package com.support.delight_android_sdk.network

import com.support.delight_android_sdk.model.DelightRequest
import com.support.delight_android_sdk.model.DelightResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("webhook/{webhookUrl}")
    suspend fun connect(@Path("webhookUrl") webhookUrl: String) : String

    @FormUrlEncoded
    @POST("webhook/{webhookUrl}")
    suspend fun getDelightResponse(@Body requestBody: DelightRequest, @Path("webhookUrl") webhookUrl: String): DelightResponse
}