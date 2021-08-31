package com.support.delight_android_sdk

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.support.delight_android_sdk.databinding.FragmentBottomDialogBinding
import com.support.delight_android_sdk.databinding.FragmentChatBinding

open class BottomDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomDialogBinding? = null
    private val binding get() = _binding!!
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBottomDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}