package com.guido.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.guido.app.MyApp.Companion.userCurrentLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    companion object{
        val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationClient: LocationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationClient = DefaultLocationClient(
            this,
            LocationServices.getFusedLocationProviderClient(this)
        )
        // Check if the location permission is already granted

    }

    // Callback method to handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                // Get the current location
                getCurrentLocation()
            } else {
                // Location permission denied
                // Handle the denied permission case
            }
        }
    }

    fun getCurrentLocation() {
        lifecycleScope.launch {
            val location = locationClient.getCurrentLocation()

        }

    }
}