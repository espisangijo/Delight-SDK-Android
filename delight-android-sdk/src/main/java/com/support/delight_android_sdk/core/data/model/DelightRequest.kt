package com.support.delight_android_sdk.model

data class DelightRequest(
    val context: Context,
    val text: String
)

data class Context(
    val deviceId: String,
    val locale: String,
    val userId: String
)