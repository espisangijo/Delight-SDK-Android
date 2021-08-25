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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
        val textView = view.findViewById<TextView>(R.id.response)
        val button = view.findViewById<Button>(R.id.send_message)
        val editText = view.findViewById<EditText>(R.id.edit_text)
        button.setOnClickListener {
            changeText(editText.text.toString())
            Log.d("ChatFragment", "Button clicked")
        }
        textView.setOnClickListener {
            activity?.let {
                Toast.makeText(activity, "text", Toast.LENGTH_LONG).show()
            }
            Log.d("ChatFragment", "Text clicked")
        }
        return view
    }

    fun changeText(text: String) {
        val myText : TextView? = activity?.findViewById<TextView>(R.id.response)
        if (myText != null) {
            myText.setText(text?: "")
        }
    }
//    fun newInstance(): Fragment() {
//        val args = Bundle()
//
//        val fragment = ()
//        fragment.arguments = args
//        return fragment
//    }
}