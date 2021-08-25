package com.support.delight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.support.delight_android_sdk.ChatFragment

class MainActivity : AppCompatActivity() {

    val chatFragment = ChatFragment();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.accelerate, chatFragment)
    }

}