package com.guido.app

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>
    suspend fun getCurrentLocation(): LatLng?

    class LocationException(message: String): Exception()
}