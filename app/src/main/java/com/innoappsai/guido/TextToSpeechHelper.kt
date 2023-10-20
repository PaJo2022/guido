package com.innoappsai.guido

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechHelper(private val context: Context, private val callback: TextToSpeechCallback) {
    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set the language for the Text-to-Speech engine (you can change this to your desired language).
                val locale = Locale.US
                textToSpeech?.language = locale
            } else {
                callback.onError("Text-to-Speech initialization failed")
            }
        }
    }

    fun convertTextToSpeech(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    interface TextToSpeechCallback {
        fun onError(errorMessage: String)
    }
}
