package com.support.delight_android_sdk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.support.delight_android_sdk.model.DelightRequest
import com.support.delight_android_sdk.model.DelightResponse
import com.support.delight_android_sdk.network.ApiService
import com.support.delight_android_sdk.network.RetrofitInstance
import com.support.delight_android_sdk.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DelightViewModel(private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<DelightResponse> = MutableLiveData()

    fun getDelightResponse(request : DelightRequest, webhookUrl : String) {
        viewModelScope.launch{
            val response:DelightResponse = repository.getDelightResponse(request, webhookUrl)
            myResponse.value = response
        }
    }
}