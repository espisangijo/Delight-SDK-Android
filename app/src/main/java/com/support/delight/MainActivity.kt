package com.support.delight

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.support.delight_android_sdk.ChatFragment
import com.support.delight_android_sdk.Voice

class MainActivity : AppCompatActivity() {

    private lateinit var chatFragment: ChatFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatFragment = ChatFragment("/webhook/android/21a948ac-e835-48c6-b37c-b45257e3b6d2/")
        val ft : FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_layout, chatFragment)
        ft.commit()
    }

}