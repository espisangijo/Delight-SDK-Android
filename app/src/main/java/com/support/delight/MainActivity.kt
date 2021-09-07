package com.support.delight

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.support.delight.databinding.ActivityMainBinding
import com.support.delight_android_sdk.core.presentation.VoiceFragment

class MainActivity : AppCompatActivity(), VoiceFragment.OnFragmentInteractionListener{

    private val TAG = "MainActivity"
    private lateinit var voiceFragment: VoiceFragment
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val fr = supportFragmentManager
//        val f : VoiceFragment = fr.findFragmentById(R.id.chat_fragment) as VoiceFragment
////        chatFragment = ChatFragment("/webhook/android/21a948ac-e835-48c6-b37c-b45257e3b6d2/")
//        f.changeText("Welcome")

        voiceFragment = VoiceFragment.Builder()
            .setWebhookUrl("webhook/android/21a948ac-e835-48c6-b37c-b45257e3b6d2/")
            .build()

        showDialog()
    }
    fun showDialog() {
        voiceFragment.show(supportFragmentManager, voiceFragment.tag)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("Not yet implemented")
    }
}