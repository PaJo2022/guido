package com.innoappsai.guido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.innoappsai.guido.databinding.ActivityMainBinding
import com.innoappsai.guido.fragments.HomeFragment
import com.innoappsai.guido.generateItinerary.screens.FragmentPlaceItinerary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()

    private var homeFragment: HomeFragment? = null
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.removeAllSavedPlaces()

//        homeFragment = HomeFragment()

        val homeFragment = FragmentPlaceItinerary()
        FragmentUtils.replaceFragment(this, R.id.main_fl_id, homeFragment!!,intent?.extras)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.getString("DEEPLINK")?.let {
            if (it.equals("PLACE_ITINERARY_SCREEN", false)) {
                val itineraryId = intent.extras?.getString("ITINERARY_DB_ID") ?: return
                homeFragment?. navigateToGeneratedItinerary(itineraryId)
            } else if (it.equals("PLACE_DETAILS_SCREEN", false)) {
                val placeId = intent.extras?.getString("PLACE_ID") ?: return
                val bundle = Bundle()
                bundle.putString("PLACE_ID", placeId)
                homeFragment?.navigateToPlaceDetailsScreen(placeId)
            } else {

            }

        }
    }




}