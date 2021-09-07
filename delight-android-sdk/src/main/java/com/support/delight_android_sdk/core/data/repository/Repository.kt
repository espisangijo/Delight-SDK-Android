package com.support.delight_android_sdk.core.data.repository

import com.support.delight_android_sdk.model.DelightRequest
import com.support.delight_android_sdk.model.DelightResponse
import com.support.delight_android_sdk.network.RetrofitInstance
import retrofit2.Response

class Repository {

    suspend fun connect(webhookUrl : String) : String {
        return RetrofitInstance.api.connect(webhookUrl)
    }
    suspend fun getDelightResponse(requestBody : DelightRequest, webhookUrl : String) : Response<DelightResponse> {
        return RetrofitInstance.api.getDelightResponse(requestBody, webhookUrl)
    }
}