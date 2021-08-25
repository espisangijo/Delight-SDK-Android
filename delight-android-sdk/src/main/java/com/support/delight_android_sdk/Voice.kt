package com.support.delight_android_sdk

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.support.delight_android_sdk.network.ApiService
import com.support.delight_android_sdk.model.DelightRequest
import com.support.delight_android_sdk.model.DelightResponse
import com.support.delight_android_sdk.network.RetrofitInstance
import com.support.delight_android_sdk.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://28b9-175-159-198-83.ngrok.io/"
class Voice {


    fun connect(webhookUrl: String) {
        val base_url = "https://alfredqa.koti.ai"
        var url : String = ""
        if (webhookUrl.matches("^/webhook/android/[a-zA-Z0-9-]+".toRegex())){
            url = base_url + webhookUrl
        } else if (webhookUrl.matches("^https://alfredqa.koti.ai/webhook/android/[a-zA-Z0-9-]+".toRegex())) {
            url = webhookUrl
        }
    }

    fun sendVoice(){
        // perform tts
        // convert text to delight request
        // send request
        // return response
    }

    fun sendText(){
        // convert text to delight request
        // send request
        // return response
    }


}