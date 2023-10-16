package com.innoappsai.guido.fragments


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.DataType
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiType
import com.innoappsai.guido.model.placesUiModel.addUiType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPlacesVideModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val appPrefs: AppPrefs
) : ViewModel() {


    private var places: ArrayList<PlaceUiModel> = ArrayList()
    private val _nearByPlacesInGroup: MutableLiveData<List<PlaceTypeUiModel>> =
        MutableLiveData()
    val nearByPlacesInGroup: LiveData<List<PlaceTypeUiModel>> get() = _nearByPlacesInGroup


    fun fetchPlacesUsingUserId() {
        viewModelScope.launch {
            places =
                placesRepository.fetchPlacesUsingUserId(appPrefs.userId.toString()) as ArrayList<PlaceUiModel>
            val placesInGroupData = async { mapPlacesByType(places) }.await()
            _nearByPlacesInGroup.postValue(placesInGroupData)
        }
    }

    private fun mapPlacesByType(
        places: List<PlaceUiModel>
    ): MutableList<PlaceTypeUiModel> {
        // Create a map to store places by type
        val placeUiTypeUiModel = mutableListOf<PlaceTypeUiModel>()
        val placesGroupedByType = places.groupBy { it.superType }
        placeUiTypeUiModel.clear()
        // Iterate through the place types
        placesGroupedByType.entries.forEach { mapData ->


            // Store the list in the map
//            placeUiTypeUiModel.add(
//                PlaceTypeUiModel(
//                    mapData.key,
//                    Constants.getPlaceTypeIcon(mapData.key.toString()),
//                    places = mapData.value.addUiType(
//                        PlaceUiType.LARGE
//                    ),
//                    dataType = DataType.DATA
//                )
//            )


        }

        return placeUiTypeUiModel
    }




}