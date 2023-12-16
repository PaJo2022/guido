package com.innoappsai.guido.data.places

import androidx.room.withTransaction
import com.innoappsai.guido.api.GuidoApi
import com.innoappsai.guido.api.UserApi
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.db.MyAppDataBase
import com.innoappsai.guido.model.FullPlaceData
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.UserPlacePreferenceRequestDTO
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteDTO
import com.innoappsai.guido.model.place_autocomplete.PlaceAutoCompleteLatLngDTO
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
import com.innoappsai.guido.model.toUiModel
import com.innoappsai.guido.utils.Resource
import com.innoappsai.guido.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BackendPlacesRepositoryImpl @Inject constructor(
    private val api: GuidoApi,
    private val userApi: UserApi,
    private val db: MyAppDataBase,
    private val appPrefs: AppPrefs
) : PlacesRepository {
    override suspend fun fetchPlacesNearMeAndSaveInLocalDb(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>,
        shouldCache : Boolean
    ): Resource<List<PlaceDTO>> {
        val response = api.fetchPlacesNearMe(latitude, longitude, radius, types)
        val places =  response.body()
       return if(response.isSuccessful && places != null){
            if(shouldCache){
                db.withTransaction {
                    db.placeDao().apply {
                        deleteAllPlaces()
                        insertPlaces(places)
                    }
                }
            }
           Resource.Success(places)
       } else {
           Resource.Error(Throwable(response.message()), null)
       }


    }

    override fun getPlacesNearMeFromLocalDb() =
        db.placeDao().getAllPlaces().map { it.toPlaceUiModel() }

    override suspend fun fetchPlacesNearLocation(
        latitude: Double,
        longitude: Double,
        radius: Int,
        types: List<String>
    ): List<PlaceUiModel> =
        api.fetchPlacesNearMe(latitude, longitude, radius, types).body()?.toPlaceUiModel()
            ?: emptyList()


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
        return null
    }

    override suspend fun deletePlaceFromDB(placeId: String) {
        withContext(Dispatchers.IO) {
            db.placeDao().deletePlaceById(placeId)
        }
    }

    override suspend fun deleteAllPlacesFromDB() {
        withContext(Dispatchers.IO){
            db.placeDao().deleteAllPlaces()
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
        return if (response.isSuccessful && response.body() != null) {
            val addedPlace = response.body()!!
            db.placeDao().insertPlace(addedPlace)
            response.body()?.toPlaceUiModel()
        } else {
            null
        }
    }


    override  fun fetchSinglePlacesDetails(
        placeId: String
    ): Flow<Resource<PlaceUiModel>> {
        return flow {
            val data = api.fetchPlacesDetails(placeId).body()
            data?.let {
                emit(Resource.Success(it.toPlaceUiModel()))
            } ?: emit(Resource.Error(Throwable("Null")))
        }
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
        withContext(Dispatchers.IO){
            val userId = appPrefs.userId ?: return@withContext
            val placeSearchDistance = appPrefs.prefDistance / 1000
            userApi.updateUserPlacePreference(
                userId = userId,
                userPlacePreferenceRequestDTO = UserPlacePreferenceRequestDTO(
                    placePreferences = preferences.map { it.id },
                    placePreferenceDistance = placeSearchDistance
                )
            )
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
    override suspend fun getSearchedPlaceLatLng(placeId: String): PlaceAutoCompleteLatLngDTO? {
        return try {
            val response = api.fetchSearchedPlaceLatLng(placeId)
            response.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateAllPlacesIsCheckedAndCheckBoxFor(
        isChecked: Boolean,
        shouldShowCheckBox: Boolean
    ) {
        withContext(Dispatchers.IO){
            db.placeDao().updateAllPlacesIsCheckedAndCheckBoxFor(isChecked, shouldShowCheckBox)
        }
    }

    override suspend fun updatePlaceIsChecked(placeId: String, isChecked: Boolean) {
        withContext(Dispatchers.IO){
            db.placeDao().updatePlaceIsChecked(placeId, isChecked)
        }

    }

    override suspend fun getSelectedLandMarks() = db.placeDao().getAllSelectedPlaces().map { it.toPlaceUiModel() }


}