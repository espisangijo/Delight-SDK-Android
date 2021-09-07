package com.support.delight_android_sdk.model

data class DelightResponse(
    val helperIntent: HelperIntent,
    val shouldEndConversation: Boolean,
    val text: String
)

data class HelperIntent(
    val name: String
)