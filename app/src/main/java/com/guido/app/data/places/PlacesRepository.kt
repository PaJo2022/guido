package com.guido.app.data.places

import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.places.geoCoding.ReverseGeoCodingDTO
import com.guido.app.model.placesUiModel.PlaceUiModel
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {


    suspend fun fetchPlacesNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): List<PlaceUiModel>

    suspend fun fetchSinglePlacesDetails(
        placeId: String
    ): PlaceUiModel?


    suspend fun fetchAddressFromLatLng(
        latitude: Double,
        longitude: Double,
    ): ReverseGeoCodingDTO?

    suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>)
    suspend fun getAllSavedPlaceTypePreferences(): List<PlaceType>
    fun getAllSavedPlaceTypePreferencesFlow(): Flow<List<PlaceType>>

    suspend fun insertNewSearchedLocation(placeAutocomplete: PlaceAutocomplete)

    fun getSearchedLocations(): Flow<List<PlaceAutocomplete>>
}