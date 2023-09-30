package com.innoappsai.guido.data.places

import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.places.geoCoding.ReverseGeoCodingDTO
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {


    suspend fun fetchPlacesNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): List<PlaceUiModel>

    suspend fun addPlace(
        placeRequestDTO: PlaceRequestDTO
    ): PlaceUiModel?


    suspend fun fetchSinglePlacesDetails(
        placeId: String
    ): PlaceUiModel?


    suspend fun fetchAddressFromLatLng(
        latitude: Double,
        longitude: Double,
    ): ReverseGeoCodingDTO?

    suspend fun fetchPlaceAutoCompleteSuggestion(
       query : String
    ): List<PlaceAutoCompleteDTO>

    suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>)
    suspend fun getAllSavedPlaceTypePreferences(): List<PlaceType>
    fun getAllSavedPlaceTypePreferencesFlow(): Flow<List<PlaceType>>

    suspend fun insertNewSearchedLocation(placeAutocomplete: PlaceAutocomplete)

    fun getSearchedLocations(): Flow<List<PlaceAutocomplete>>
}