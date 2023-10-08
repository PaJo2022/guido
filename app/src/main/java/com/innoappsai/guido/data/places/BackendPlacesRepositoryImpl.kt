package com.innoappsai.guido.data.places

import androidx.room.withTransaction
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.FullPlaceData
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
import com.innoappsai.guido.model.toUiModel
import com.innoappsai.guido.utils.Resource
import com.innoappsai.guido.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BackendPlacesRepositoryImpl @Inject constructor(
    private val api: GuidoApi,
    private val db: MyAppDataBase
) : PlacesRepository {
    override suspend fun fetchPlacesNearMeAndSaveInLocalDb(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ) {
        val places = api.fetchPlacesNearMe(latitude, longitude, radius, types).body()
        db.withTransaction {
            db.placeDao().apply {
                deleteAllPlaces()
                insertPlaces(places)
            }
        }
    }

    override fun getPlacesNearMeFromLocalDb() =
        db.placeDao().getAllPlaces().map { it.toPlaceUiModel() }


    override suspend fun fetchPlacesNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): Flow<Resource<List<PlaceUiModel>>> {
        return networkBoundResource(
            query = {
                db.placeDao().getAllPlaces().map { it.toPlaceUiModel() }
            },
            fetch = {
                api.fetchPlacesNearMe(latitude, longitude, radius, types).body()
            },
            saveFetchResult = { places ->
                db.withTransaction {
                    db.placeDao().deleteAllPlaces()
                    db.placeDao().insertPlaces(places)
                }
            }
        )
    }

    override suspend fun fetchPlacesUsingUserId(userId: String): List<PlaceUiModel> {
        val response = api.fetchPlacesByUserId(userId)
        return if (response.isSuccessful) {
            response.body()?.toPlaceUiModel() ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun deletePlaceById(userId: String, placeId: String): PlaceDTO? {
        val response = api.deletePlaceById(userId, placeId)
        if (response.isSuccessful && response.body() != null) {
            db.placeDao().deletePlaceById(placeId)
        }
        return response.body()
    }

    override suspend fun deletePlaceFromDB(placeId: String) {
        withContext(Dispatchers.IO) {
            db.placeDao().deletePlaceById(placeId)
        }
    }

    override suspend fun updatePlaceStaticMapByPlaceId(
        placeId: String,
        staticMapUrl: String
    ): PlaceUiModel? {
        val response = api.updatePlaceStaticMapUrlPlaceById(placeId, staticMapUrl)
        return response.body()?.toPlaceUiModel()
    }

    override suspend fun addPlace(placeRequestDTO: PlaceRequestDTO): PlaceUiModel? {
        val response = api.addPlace(placeRequestDTO)
        return if (response.isSuccessful) {
            response.body()?.toPlaceUiModel()
        } else {
            null
        }
    }


    override  fun fetchSinglePlacesDetails(
        placeId: String
    ): Flow<Resource<PlaceUiModel>> {
        return networkBoundResource(
            query = {
                db.placeDao().getPlaceById(placeId).map { it.toPlaceUiModel() }
            },
            fetch = {
                api.fetchPlacesDetails(placeId).body()
            },
            shouldFetch = {
                it.serverDbId == null
            },
            saveFetchResult = { place ->
                db.withTransaction {
                    db.placeDao().updatePlace(place)
                }
            }
        )
    }

    override suspend fun fetchAddressFromLatLng(
        latitude: Double,
        longitude: Double,
    ): FullPlaceData? {
        return try {
            val response = api.fetchAddressFromLatLng(latitude, longitude)
            response.body()?.toUiModel()
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