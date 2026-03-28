package com.yourcompany.arnav.voice

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File

class VoskRecognizer(
    private val context: Context,
    private val modelPath: String
) {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isListening = false
    
    companion object {
        const val SAMPLE_RATE = 16000
    }
    
    init {
        loadModel()
    }
    
    private fun loadModel() {
        try {
            val file = File(modelPath)
            if (!file.exists()) {
                throw IllegalStateException("Vosk model not found at: $modelPath")
            }
            model = Model(modelPath)
        } catch (e: Exception) {
            throw RuntimeException("Failed to load Vosk model", e)
        }
    }
    
    fun startListening(onResult: (String) -> Unit) {
        if (isListening) return
        
        isListening = true
        
        try {
            val recognizer = Recognizer(model, SAMPLE_RATE.toFloat())
            speechService = SpeechService(recognizer, SAMPLE_RATE)
            
            speechService?.startListening(object : RecognitionListener {
                override fun onPartialResult(hypothesis: String?) {
                    // Handle partial results if needed
                }
                
                override fun onResult(hypothesis: String?) {
                    hypothesis?.let {
                        val text = parseResult(it)
                        if (text.isNotBlank()) {
                            onResult(text)
                        }
                    }
                }
                
                override fun onFinalResult(hypothesis: String?) {
                    hypothesis?.let {
                        val text = parseResult(it)
                        if (text.isNotBlank()) {
                            onResult(text)
                        }
                    }
                }
                
                override fun onError(exception: Exception?) {
                    exception?.printStackTrace()
                }
                
                override fun onTimeout() {
                    // Restart listening
                    if (isListening) {
                        speechService?.startListening(this)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            isListening = false
        }
    }
    
    private fun parseResult(json: String): String {
        // Parse Vosk JSON output: {"text": "hello world"}
        return try {
            val regex = """"text"\s*:\s*"([^"]*)"""".toRegex()
            val match = regex.find(json)
            match?.groupValues?.get(1) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    fun stopListening() {
        isListening = false
        speechService?.stop()
        speechService = null
    }
    
    fun release() {
        stopListening()
        scope.cancel()
        model?.close()
        model = null
    }
}
