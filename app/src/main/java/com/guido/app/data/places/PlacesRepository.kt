package com.guido.app.data.places

import com.guido.app.model.PlaceType
import com.guido.app.model.places.geoCoding.ReverseGeoCodingResponse
import com.guido.app.model.placesUiModel.PlaceUiModel
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {

    suspend fun fetchPlacesNearMe(
        location: String,
        radius: Int,
        type: String,
        keyword: String,
        key: String,
    ): List<PlaceUiModel>

    suspend fun fetchAddressFromLatLng(
        latLng: String,
        key: String,
    ): ReverseGeoCodingResponse?

    suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>)
    suspend fun getAllSavedPlaceTypePreferences(): Flow<List<PlaceType>>
}