package com.guido.app.data.places

import android.util.Log
import androidx.room.withTransaction
import com.guido.app.api.GuidoApi
import com.guido.app.db.MyAppDataBase
import com.guido.app.model.PlaceType
import com.guido.app.model.places.geoCoding.ReverseGeoCodingResponse
import com.guido.app.model.places.toPlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.singlePlaceDetails.toPlaceUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val api: GuidoApi,
    private val db : MyAppDataBase
) : PlacesRepository {
    override suspend fun fetchPlacesNearMe(
        location: String,
        radius: Int,
        type: String,
        keyword: String,
        key: String
    ): List<PlaceUiModel> {
        return try {
            val response = api.fetchPlacesNearMe(location, radius, keyword, key)
            if (response.isSuccessful) {
                response.body()?.results?.toPlaceUiModel() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.i("JAPAN", "DATA $e")
            emptyList()
        }
    }

    override suspend fun fetchSinglePlacesDetails(
        placeId: String,
        key: String
    ): PlaceUiModel? {
        return try {
            val response = api.fetchPlacesDetails(placeId, key)
            if (response.isSuccessful) {
                response.body()?.result?.toPlaceUiModel()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.i("JAPAN", "DATA $e")
            null
        }
    }

    override suspend fun fetchAddressFromLatLng(
        latLng: String,
        key: String
    ): ReverseGeoCodingResponse? {
        return try {
            val response = api.fetchAddressFromLatLng(latLng, key)
            response.body()
        } catch (e: Exception) {
            null
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

    override suspend fun getAllSavedPlaceTypePreferences()  = db.placeTypeDao().getAllPlaceTypes()
    override fun getAllSavedPlaceTypePreferencesFlow()  = db.placeTypeDao().getAllPlaceTypesFlow()


}