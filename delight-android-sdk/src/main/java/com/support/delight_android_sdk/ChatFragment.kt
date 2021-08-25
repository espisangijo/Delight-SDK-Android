package com.support.delight_android_sdk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.support.delight_android_sdk.model.DelightResponse
import com.support.delight_android_sdk.repository.Repository
import org.w3c.dom.Text


class ChatFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: DelightViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val viewModelFactory = DelightViewModelFactory(repository)
        var viewModel = ViewModelProvider(this, viewModelFactory).get(DelightViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_chat, container, false)
        val textView = view.findViewById<TextView>(R.id.text_view)
        val buttonView = view.findViewById<Button>(R.id.send_message)
        val editTextView = view.findViewById<EditText>(R.id.text_input_edittext)

        buttonView.setOnClickListener {
            changeText(editTextView.text.toString())
            Log.d("ChatFragment","Button Clicked")
        }

        viewModel.myResponse.observe(this, object : Observer<DelightResponse?> {
            override fun onChanged(t: DelightResponse?) {
                changeText(t?.text)
                Log.d("ChatFragment", "Change")
            }
        })

        return view
    }

    fun changeText(text: String?) {
        activity?.findViewById<TextView>(R.id.text_view)?.text = text ?: ""
    }
}