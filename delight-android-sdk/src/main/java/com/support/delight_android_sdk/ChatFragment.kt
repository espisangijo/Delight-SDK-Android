package com.support.delight_android_sdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.support.delight_android_sdk.databinding.FragmentChatBinding
import com.support.delight_android_sdk.model.Message
import com.support.delight_android_sdk.repository.Repository
import com.support.delight_android_sdk.utils.Constants.Companion.RECEIVE_ID
import com.support.delight_android_sdk.utils.Constants.Companion.SEND_ID
import com.support.delight_android_sdk.utils.Time
import kotlinx.coroutines.*
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.R.attr.data
import android.annotation.SuppressLint


class ChatFragment (webhook: String) : BottomSheetDialogFragment() {

    private val webhookUrl =
        if(webhook.startsWith('/'))
            webhook.slice(1 until webhook.length)
        else
            webhook

    private val TAG = "ChatFragment"
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DelightViewModel


    private lateinit var adapter: MessagingAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var speechRecognizer : SpeechRecognizer
    private lateinit var speechRecognizerIntent : Intent

    private var mediaRecorder: MediaRecorder? = null
    private val recordPermission : String = Manifest.permission.RECORD_AUDIO
    private var isRecording: Boolean = false
    private lateinit var recordFile : String
    private var listItems = mutableListOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        listItems.removeAt(0)
        val repository = Repository()
        val viewModelFactory = DelightViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DelightViewModel::class.java)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root

        Log.d(TAG, SpeechRecognizer.isRecognitionAvailable(requireContext()).toString())

        adapter = MessagingAdapter()
        recyclerView = binding.rvMessages
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                Log.d(TAG, "onReadyForSpeech")
                Toast.makeText(context, "Talk now", Toast.LENGTH_SHORT).show()
            }
            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(v: Float) {
            }

            override fun onBufferReceived(bytes: ByteArray) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                var errorCode = ""
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> errorCode = "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> errorCode = "Other client side errors"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> errorCode = "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> errorCode = "Network related errors"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> errorCode = "Network operation timed out"
                    SpeechRecognizer.ERROR_NO_MATCH -> errorCode = "No recognition result matched"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> errorCode = "RecognitionService busy"
                    SpeechRecognizer.ERROR_SERVER -> errorCode = "Server sends error status"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> errorCode = "No speech input"
                }
                Log.d(TAG,errorCode)
            }

            override fun onResults(results: Bundle) {
                Log.d(TAG, "onResults")

                val result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                Log.i(TAG, "onResults" + result.toString())
                listItems.add(result.toString())
                if (listItems.count() > 10){
                    listItems.removeAt(0)
                }
                var text = ""
                listItems.forEach {
                    text += it + "\n"
                }
                try {
                    binding?.etMessage.setText(text)
                    speechRecognizer?.startListening(speechRecognizerIntent)
                } catch (ex: Exception) {

                }
            }
            override fun onPartialResults(partialResults: Bundle) {
                Log.d(TAG, "onPartialResults")
                val result = partialResults.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                Log.i(TAG, "onPartialResults" + result.toString())

            }
            override fun onEvent(i: Int, bundle: Bundle) {
                Log.d(TAG, "onEvent")}
        })

        binding?.btnAudio.setOnTouchListener { v, event ->

            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(checkPermission()){
                        Log.d(TAG, "Button down")
                        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                        speechRecognizerIntent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

                        GlobalScope.launch {
                            activity?.runOnUiThread{
                                speechRecognizer.startListening(speechRecognizerIntent)
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "Button up")

                    GlobalScope.launch {
                        activity?.runOnUiThread {
                            speechRecognizer.stopListening()
                        }
                    }
                }
            }

            v?.onTouchEvent(event) ?: true
        }


        binding?.btnSend.setOnClickListener {
            sendMessage()
        }

        binding?.etMessage.setOnClickListener {
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        viewModel._lastResponse.observe(this, Observer {
                response ->
            if(response.isSuccessful) {
                val timeStamp = Time.timeStamp()
                val responseText = response.body()?.text.toString()
                viewModel.appendMessages(Message(responseText, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(responseText, RECEIVE_ID, timeStamp))
                recyclerView.scrollToPosition(adapter.itemCount - 1)
                Log.d(TAG, response.body()?.text.toString())
            } else {
                Log.e(TAG, "error in getting response")
            }
        })
    }

    private fun sendMessage() {
        val message = binding?.etMessage.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            binding?.etMessage.setText("")
            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            recyclerView.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()
        val request = Message(message, SEND_ID, timeStamp)
        viewModel.getDelightResponse(request, webhookUrl)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]

                        Log.d(TAG, recognizedText.toString())
                        // Do what you want with the recognized text.
                        binding?.etMessage.setText(recognizedText)
                    }
    }

    private fun checkPermission() : Boolean{
        if (ActivityCompat.checkSelfPermission(requireContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission Granted")
            return true
        }
        else {
            Log.d(TAG, "Permission Denied")
            Log.d(TAG, "Requesting Permission")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(recordPermission), hashCode())
            return false
        }
    }
    companion object {
        private const val REQUEST_CODE_STT = 1
    }

    private fun startRecording() {
        val recordPath = activity?.getExternalFilesDir("/")?.absolutePath
        recordFile = "file.3gp"
        isRecording = true
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("$recordPath/$recordFile")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Log.d(TAG,"start recording")
            start()
        }

    }

    private fun stopRecording(){
        isRecording = false
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}