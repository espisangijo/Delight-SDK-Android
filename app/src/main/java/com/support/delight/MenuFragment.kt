package com.support.delight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.delight.databinding.FragmentMenuBinding
import com.support.delight_android_sdk.BottomDialogFragment
import com.support.delight_android_sdk.ChatFragment
import com.support.delight_android_sdk.databinding.FragmentBottomDialogBinding
import com.support.delight_android_sdk.databinding.FragmentChatBinding

class MenuFragment  : BottomDialogFragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = FragmentBottomDialogBinding.inflate(inflater, container, false).root
        return view
    }
}