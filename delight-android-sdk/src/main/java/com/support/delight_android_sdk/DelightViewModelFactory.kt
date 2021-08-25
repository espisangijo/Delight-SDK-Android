package com.support.delight_android_sdk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.support.delight_android_sdk.repository.Repository

class DelightViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DelightViewModel(repository) as T
    }
}