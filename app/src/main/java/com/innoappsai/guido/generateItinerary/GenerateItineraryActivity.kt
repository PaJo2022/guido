package com.innoappsai.guido.generateItinerary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.innoappsai.guido.databinding.ActivityGenerateItineraryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateItineraryActivity : AppCompatActivity() {

    private var _binding: ActivityGenerateItineraryBinding? = null
    private val binding: ActivityGenerateItineraryBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGenerateItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}