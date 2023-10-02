package com.innoappsai.guido.data.places

import androidx.room.withTransaction
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.places.geoCoding.ReverseGeoCodingDTO
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BackendPlacesRepositoryImpl @Inject constructor(
    private val api: GuidoApi,
    private val db: MyAppDataBase
) : PlacesRepository {


    override suspend fun fetchPlacesNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): List<PlaceUiModel> {
        val response = api.fetchPlacesNearMe(latitude, longitude, radius, types)
        return if (response.isSuccessful) {
            response.body()?.toPlaceUiModel() ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun fetchPlacesUsingUserId(userId: String): List<PlaceUiModel> {
        val response = api.fetchPlacesByUserId(userId)
        return if (response.isSuccessful) {
            response.body()?.toPlaceUiModel() ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun addPlace(placeRequestDTO: PlaceRequestDTO): PlaceUiModel? {
        val response = api.addPlace(placeRequestDTO)
        return if (response.isSuccessful) {
            response.body()?.toPlaceUiModel()
        } else {
            null
        }
    }


    override suspend fun fetchSinglePlacesDetails(
        placeId: String
    ): PlaceUiModel? {
        return try {
            val response = api.fetchPlacesDetails(placeId)
            if (response.isSuccessful) {
                response.body()?.toPlaceUiModel()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchAddressFromLatLng(
        latitude: Double,
        longitude: Double,
    ): ReverseGeoCodingDTO? {
        return try {
            val response = api.fetchAddressFromLatLng(latitude, longitude)
            response.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun fetchPlaceAutoCompleteSuggestion(query: String): List<PlaceAutoCompleteDTO> {
        return try {
            val response = api.fetchPlaceAutoCompleteSuggestion(query)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun saveFavouritePlacePreferences(preferences: List<PlaceType>) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                db.placeTypeDao().apply {
                    deletePlaceTypes()
                    preferences.forEach {
                        insertNewPlaceType(it)
                    }
                }
            }
        }
    }

    override suspend fun getAllSavedPlaceTypePreferences() = db.placeTypeDao().getAllPlaceTypes()
    override fun getAllSavedPlaceTypePreferencesFlow() = db.placeTypeDao().getAllPlaceTypesFlow()
    override suspend fun insertNewSearchedLocation(placeAutocomplete: PlaceAutocomplete) {
        withContext(Dispatchers.IO) {
            db.locationSearchDao().insertSearchedLocation(placeAutocomplete)
        }
    }

    override fun getSearchedLocations() = db.locationSearchDao().getRecentSearchLocations()


}