package com.support.delight_android_sdk.core.presentation

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.support.delight_android_sdk.databinding.FragmentVoiceBinding
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.support.delight_android_sdk.R
import com.support.delight_android_sdk.core.data.repository.Repository
import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.support.delight_android_sdk.core.data.Constants.RECEIVE_ID
import com.support.delight_android_sdk.core.data.Constants.SEND_ID
import com.support.delight_android_sdk.model.Message
import com.support.delight_android_sdk.utils.Time
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class VoiceFragment private constructor(val webhookUrl: String?): BottomSheetDialogFragment(), TextToSpeech.OnInitListener {
    data class Builder(
        var webhookUrl: String? = null
    ) {
        fun setWebhookUrl(webhookUrl: String) = apply {this.webhookUrl = webhookUrl}
        fun build() = VoiceFragment(webhookUrl)
    }

    private var tts: TextToSpeech? = null
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "LANG MISSING OR NOT SUPPORTED")
            }

            tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "on TTS start")
                }

                override fun onDone(utteranceId: String?) {

                    Log.d(TAG, "on TTS done")
                    activity?.runOnUiThread {

                        startListening()
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.d(TAG, "on TTS error")
                }
            })
        }
        else {
            Log.e(TAG, "Fail init TTS")
        }
    }

    private val recordPermission : String = Manifest.permission.RECORD_AUDIO

    private lateinit var speechRecognizer : SpeechRecognizer
    private lateinit var speechRecognizerIntent : Intent

    private val TAG = "VoiceFragment"
    private var mListener: OnFragmentInteractionListener? = null

    private var _binding: FragmentVoiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DelightViewModel

    private val MY_PERMISSIONS_RECORD_AUDIO = 1

    private lateinit var adapter: MessagingAdapter
    private lateinit var recyclerView: RecyclerView
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog")
        return BottomSheetDialog(requireContext(), theme)
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // turn off
        speechRecognizer.cancel()
        speechRecognizer.destroy()
    }



    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        if(checkPermission()) {
            initSpeechRecognition()
            initTTS()
        }
        val repository = Repository()
        val viewModelFactory = DelightViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DelightViewModel::class.java)

    }
    private fun initTTS(){
        tts = TextToSpeech(activity,this)
    }

    private fun initSpeechRecognition() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        Log.d(TAG, "speech recognizer created")
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                Log.d(TAG, "onReadyForSpeech")
                Toast.makeText(context, "Talk now", Toast.LENGTH_SHORT).show()
            }
            override fun onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(v: Float) {
                when {
                    v > 1 -> {
                        binding?.btnAudio.setImageResource(R.drawable.logo)
                    }
                    v > 3 -> {
                        binding?.btnAudio.setImageResource(R.drawable.logo)
                    }
                    else -> {
                        binding?.btnAudio.setImageResource(R.drawable.logo)
                    }
                }
            }

            override fun onBufferReceived(bytes: ByteArray) {
                Log.d(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech")
//                if (adapter.messagesList.size === 0) {

//                }
            }

            override fun onError(error: Int) {
                var errorCode = ""
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> {
                        errorCode = "Audio recording error"
                        binding?.requestText.text = "Unable to record audio"
                    }
                    SpeechRecognizer.ERROR_CLIENT -> {
                        errorCode = "Other client side errors"
                        binding?.requestText.text = "Service unavailable"
                    }
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        errorCode = "Insufficient permissions"
                        binding?.requestText.text = "Insufficient permission"
                    }
                    SpeechRecognizer.ERROR_NETWORK -> {
                        errorCode = "Network related errors"
                        binding?.requestText.text = "Network error"
                    }
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        errorCode = "Network operation timed out"
                        binding?.requestText.text = "Network timed out"
                    }
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        errorCode = "No recognition result matched"
                        binding?.requestText.text = "Try saying \"Hello!\""
                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                        errorCode = "RecognitionService busy"
                        binding?.requestText.text = "Recognition service is currently used by other app"
                    }
                    SpeechRecognizer.ERROR_SERVER -> errorCode = "Server sends error status"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                        errorCode = "No speech input"
                        binding?.requestText.text = "No speech input"
                    }
                }
                Log.d(TAG,errorCode)
            }

            override fun onResults(results: Bundle) {
                Log.d(TAG, "onResults")
                val result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (result != null) {
                    changeText(result.get(0).toString())
                }
                adapter.insertMessage(Message(binding?.requestText.text.toString(), SEND_ID, Time.timeStamp() ))
                recyclerView.scrollToPosition(adapter.itemCount - 1)
                sendMessage()
            }
            override fun onPartialResults(partialResults: Bundle) {
                binding?.requestText.alpha = 1.0.toFloat()
                Log.d(TAG, "onPartialResults")
                val result = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                changeText(result.toString().slice(1 until result.toString().length -1 ))
            }
            override fun onEvent(i: Int, bundle: Bundle) {
                Log.d(TAG, "onEvent")}
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _binding = FragmentVoiceBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = MessagingAdapter()
        recyclerView = binding.rvMessages
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(activity)
        return view
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding?.bottomSheetFragment)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;
//        binding?.bottomSheetFragment.removeView(binding?.rvMessages)
//        binding?.bottomSheetFragment.addView(binding?.constraintLayout)
        bottomSheetBehavior.isDraggable= false
//        bottomSheetBehavior.peekHeight = 700
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
                Log.d(TAG, "on slide")
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d(TAG, "dragging")
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        Log.d(TAG, "expanded")
//
//                        val params: ViewGroup.LayoutParams = recyclerView.layoutParams
//                        params.height = 500
//                        recyclerView.layoutParams = params
//                    }
//                    BottomSheetBehavior.STATE_DRAGGING -> {
//                        Log.d(TAG, "dragging")
//                        Toast.makeText(requireContext(), "STATE_DRAGGING", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    BottomSheetBehavior.STATE_SETTLING -> Toast.makeText(requireContext(), "STATE_SETTLING", Toast.LENGTH_SHORT).show()
//                    BottomSheetBehavior.STATE_HIDDEN -> Toast.makeText(requireContext(), "STATE_HIDDEN", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(requireContext(), "OTHER_STATE", Toast.LENGTH_SHORT).show()
                }
            }
        })

        startListening()

        binding?.btnAudio.setOnClickListener {
            tts?.stop();
            startListening()
        }
        viewModel._messages.observe(this, Observer {
            data ->
                Log.d(TAG, data.toString())
        })
        viewModel._lastResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                Log.d(TAG, response.body().toString())
                binding?.requestText.text = ""
                adapter.insertMessage(Message(response.body()?.text.toString(), RECEIVE_ID, Time.timeStamp()))
                recyclerView.scrollToPosition(adapter.itemCount - 1)
                speakOut(response.body()?.text.toString())
                Log.d(TAG, response.body()?.text.toString())
            } else {
                Log.e(TAG, "error in getting response")
            }
        }
        )
    }

    private fun startListening() {
        if(checkPermission()) {
            Log.d(TAG, checkPermission().toString())
            try {
                GlobalScope.launch {
                    binding?.requestText.alpha = 0.3.toFloat()
                    binding?.requestText.text = "Listening..."

                    activity?.runOnUiThread {

                        speechRecognizer.startListening(speechRecognizerIntent)
                    }
                }
            } catch (e: Exception) {

                GlobalScope.launch {
                    activity?.runOnUiThread {
                        speechRecognizer.stopListening()
                    }
                }
            }

        }
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun changeText(newText: String) {
        binding?.requestText.text = newText
    }

    fun sendMessage() {
        if (webhookUrl === null) {
            Log.e(TAG, "invalid webhook")
        } else {
            viewModel.getDelightResponse(Message(binding?.requestText.text.toString(), SEND_ID, Time.timeStamp()), webhookUrl)
        }

    }

    private fun checkPermission() : Boolean{
        return if (ContextCompat.checkSelfPermission(requireContext(), recordPermission) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission Granted")
            true
        } else {
            Toast.makeText(context, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET),
                MY_PERMISSIONS_RECORD_AUDIO);

            false
        }
    }

    override fun onDestroy() {
        if(tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        if (speechRecognizer != null) {
            speechRecognizer!!.cancel()
            speechRecognizer!!.destroy()
        }
        super.onDestroy()
    }


}