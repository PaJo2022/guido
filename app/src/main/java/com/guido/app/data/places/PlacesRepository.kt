package com.guido.app.data.places

import com.guido.app.model.PlaceAutocomplete
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

    suspend fun fetchSinglePlacesDetails(
        placeId: String,
        key: String,
    ): PlaceUiModel?


    suspend fun fetchAddressFromLatLng(
        latLng: String,
        key: String,
    ): ReverseGeoCodingResponse?

    suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>)
    suspend fun getAllSavedPlaceTypePreferences(): List<PlaceType>
    fun getAllSavedPlaceTypePreferencesFlow(): Flow<List<PlaceType>>

    suspend fun insertNewSearchedLocation(placeAutocomplete: PlaceAutocomplete)

    fun getSearchedLocations(): Flow<List<PlaceAutocomplete>>
}