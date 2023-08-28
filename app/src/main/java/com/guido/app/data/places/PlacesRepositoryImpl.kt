package com.guido.app.data.places

import android.util.Log
import com.guido.app.api.GuidoApi
import com.guido.app.model.places.toPlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val api: GuidoApi
) : PlacesRepository {
    override suspend fun fetchPlacesNearMe(
        location: String,
        radius: Int,
        type: String,
        keyword: String?,
        key: String
    ): List<PlaceUiModel> {
        return try {
            val response = api.fetchPlacesNearMe(location, radius, type, key)
            Log.i("JAPAN", "DATA $response")
            if(response.isSuccessful){
                response.body()?.results?.toPlaceUiModel() ?: emptyList()
            }else{
                emptyList()
            }
        }catch (e : Exception){
            Log.i("JAPAN", "DATA $e")
            emptyList()
        }
    }


}