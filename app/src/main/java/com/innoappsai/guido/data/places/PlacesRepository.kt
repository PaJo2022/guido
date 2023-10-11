package com.innoappsai.guido.data.places

import com.innoappsai.guido.model.FullPlaceData
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    suspend fun fetchPlacesNearMeAndSaveInLocalDb(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ) : Resource<List<PlaceDTO>>

    fun getPlacesNearMeFromLocalDb(): Flow<List<PlaceUiModel>>

    suspend fun fetchPlacesNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): Flow<Resource<List<PlaceUiModel>>>

    suspend fun fetchPlacesUsingUserId(
        userId: String
    ): List<PlaceUiModel>

    suspend fun deletePlaceById(
        userId: String,
        placeId: String
    ): PlaceDTO?

    suspend fun deletePlaceFromDB(
        placeId: String
    )

    suspend fun updatePlaceStaticMapByPlaceId(
        placeId: String,
        staticMapUrl : String
    ): PlaceUiModel?

    suspend fun addPlace(
        placeRequestDTO: PlaceRequestDTO
    ): PlaceUiModel?


     fun fetchSinglePlacesDetails(
        placeId: String
    ): Flow<Resource<PlaceUiModel>>




    suspend fun fetchAddressFromLatLng(
        latitude: Double,
        longitude: Double,
    ): FullPlaceData?

    suspend fun fetchPlaceAutoCompleteSuggestion(
       query : String
    ): List<PlaceAutoCompleteDTO>

    suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>)
    suspend fun getAllSavedPlaceTypePreferences(): List<PlaceType>
    fun getAllSavedPlaceTypePreferencesFlow(): Flow<List<PlaceType>>

    suspend fun insertNewSearchedLocation(placeAutocomplete: PlaceAutocomplete)

    fun getSearchedLocations(): Flow<List<PlaceAutocomplete>>
}