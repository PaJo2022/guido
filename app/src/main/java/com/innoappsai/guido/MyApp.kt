package com.innoappsai.guido

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.MutableLiveData
import androidx.work.Configuration
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.review.ReviewRequestDTO
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    companion object{
        var itineraryGenerationMessage: String = ""
        val isMapFetched: Boolean = false
        var userCurrentLatLng: LatLng? = null
        var isCurrentLocationFetched: Boolean = false
        var userCurrentLocation: MutableSharedFlow<Pair<Double, Double>> = MutableSharedFlow()
        var userCurrentFormattedAddress: String? = null
        var currentCountry: String? = null
        var currentPlace: String? = null
        var isPrefUpdated: MutableLiveData<Boolean> = MutableLiveData(false)


        var placeRequestDTO : PlaceRequestDTO?= null
        var reviewRequestDTO: ReviewRequestDTO? = null
        var tempPlaceInterestes: List<PlaceType> ?= null
        var tempPlaceDistance: Int ?= null
        var isNewInterestsSet: Boolean = false

        var isHyperLocalServiceIsRunning : MutableLiveData<Boolean> = MutableLiveData()
    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // Initialize the Places API with your API key
        Places.initialize(applicationContext, "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY")
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
