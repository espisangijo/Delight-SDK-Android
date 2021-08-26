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
import com.support.delight_android_sdk.model.Context
import com.support.delight_android_sdk.model.DelightRequest
import com.support.delight_android_sdk.model.DelightResponse
import com.support.delight_android_sdk.repository.Repository
import com.support.delight_android_sdk.utils.Constants.Companion.WEBHOOK_URL
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
        val textView = view.findViewById<TextView>(R.id.response)
        val button = view.findViewById<Button>(R.id.send_message)
        val editText = view.findViewById<EditText>(R.id.edit_text)

        button.setOnClickListener {
            val delightRequest = textToDelightRequest(editText.text.toString())
            viewModel.getDelightResponse(delightRequest, WEBHOOK_URL)

            Log.d("ChatFragment", "Button clicked")
        }
        textView.setOnClickListener {
            activity?.let {
                Toast.makeText(activity, "text", Toast.LENGTH_LONG).show()
            }
            Log.d("ChatFragment", "Text clicked")
        }

        viewModel.myResponse.observe(this, Observer {
            response ->
                if (response.isSuccessful) {
                    Log.d("ChatFragment", response.body()?.text.toString())
                } else {
                    Log.e("ChatFragment", response.errorBody().toString())
                }

        })
        return view
    }

    fun textToDelightRequest(text: String) : DelightRequest {
        val deviceId = "6ffc44869276d009"
        val userId = ""
        val locale = "en"
        var request = DelightRequest(Context(deviceId, userId, locale), text)
        return request
    }
    fun changeText(text: String) {
        val myText : TextView? = activity?.findViewById<TextView>(R.id.response)
        if (myText != null) {
            myText.setText(text?: "")
        }
    }
}