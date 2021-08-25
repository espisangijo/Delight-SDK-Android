package com.support.delight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.support.delight_android_sdk.ChatFragment
import com.support.delight_android_sdk.Voice

class MainActivity : AppCompatActivity() {

    private lateinit var chatFragment: ChatFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatFragment = ChatFragment()
        val ft : FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_layout, chatFragment)
        ft.commit()
    }

}