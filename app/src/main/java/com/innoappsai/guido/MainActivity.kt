package com.innoappsai.guido

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.innoappsai.guido.databinding.ActivityMainBinding
import com.innoappsai.guido.fragments.HomeFragment
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

        homeFragment = HomeFragment()

        //val homeFragment = FragmentPlaceItinerary()
        FragmentUtils.replaceFragment(this, R.id.main_fl_id, homeFragment!!, intent?.extras)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("JAPAN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            viewModel.setFcmKey(token)
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getDeepLink(intent)
    }

    private fun getDeepLink(intent: Intent?) {
        Log.i("JAPAN", "ITINERARY_ID: ${intent}")
        if (Intent.ACTION_VIEW == intent?.action) {
            // Get the Uri from the intent data
            val uri: Uri? = intent.data
            when(uri?.path){
                "/itinerary"->{
                    val itineraryId: String = uri.getQueryParameter("id").toString()
                    homeFragment?.navigateToItineraryDetailsScreen(itineraryId)
                }
            }
        }else{
            intent?.extras?.getString("DEEPLINK")?.let {
                if (it.equals("PLACE_ITINERARY_SCREEN", true)) {
                    val itineraryId = intent.extras?.getString("ITINERARY_DB_ID") ?: return
                    homeFragment?.navigateToGeneratedItinerary(itineraryId)
                } else if (it.equals("PLACE_DETAILS_SCREEN", false)) {
                    val placeId = intent.extras?.getString("PLACE_ID") ?: return
                    val bundle = Bundle()
                    bundle.putString("PLACE_ID", placeId)
                    homeFragment?.navigateToPlaceDetailsScreen(placeId)
                } else if (it.equals("ITINERARY_GENERATED", true)) {
                    val itineraryId = intent.extras?.getString("VALUE") ?: return
                    val bundle = Bundle()
                    bundle.putString("ITINERARY_ID", itineraryId)

                } else {

                }

            }
        }


    }


}