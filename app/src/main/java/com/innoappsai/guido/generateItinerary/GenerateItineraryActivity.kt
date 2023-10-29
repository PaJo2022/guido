package com.innoappsai.guido.generateItinerary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.innoappsai.guido.databinding.ActivityGenerateItineraryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateItineraryActivity : AppCompatActivity() {

    private val viewModel : ViewModelGenerateItinerary by viewModels()
    companion object {
        fun startActivity(context: Context,placeName : String,placeAddress : String) {
            val intent = Intent(context, GenerateItineraryActivity::class.java)
            intent.putExtra("PLACE_NAME",placeName)
            intent.putExtra("PLACE_ADDRESS",placeAddress)
            context.startActivity(intent)
        }
    }

    private var _binding: ActivityGenerateItineraryBinding? = null
    private val binding: ActivityGenerateItineraryBinding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGenerateItineraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val placeName = intent.getStringExtra("PLACE_NAME")
        val placeAddress = intent.getStringExtra("PLACE_ADDRESS")

        viewModel.selectedPlaceName = placeName.toString()
        viewModel.selectedPlaceAddress = placeAddress.toString()
    }


}