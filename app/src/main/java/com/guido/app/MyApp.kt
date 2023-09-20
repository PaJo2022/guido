package com.guido.app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.MutableSharedFlow

@HiltAndroidApp
class MyApp : Application() {

    companion object{
        var googleMap: GoogleMap? = null
        var userCurrentLatLng: LatLng? = null
        var isCurrentLocationFetched : Boolean = false
         var userCurrentLocation : MutableSharedFlow<Pair<Double,Double>> = MutableSharedFlow()
         var userCurrentFormattedAddress : String ?= null
        var isPrefUpdated : MutableLiveData<Boolean> = MutableLiveData(false)
    }
    override fun onCreate() {
        super.onCreate()

        // Initialize the Places API with your API key
        Places.initialize(applicationContext, "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY")
    }
}
