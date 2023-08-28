package com.guido.app.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.guido.app.data.places.PlacesRepository
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(private val placesRepository: PlacesRepository) :
    ViewModel() {

    private val _nearByPlaces: MutableStateFlow<List<PlaceUiModel>> = MutableStateFlow(emptyList())
    val nearByPlaces: StateFlow<List<PlaceUiModel>> get() = _nearByPlaces.asStateFlow()

    private val placeFields = listOf(
        Place.Field.NAME,
        Place.Field.BUSINESS_STATUS,
        Place.Field.ID,
        Place.Field.LAT_LNG,
    )



    @SuppressLint("MissingPermission")
    fun fetchPlacesDetailsNearMe(
        location : String,
        radius : Int,
        type : String,
        keyword : String?=null,
        key : String,
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val nearByAttractions = placesRepository.fetchPlacesNearMe(
                location, radius, type, keyword, key
            )
            _nearByPlaces.emit(nearByAttractions)
        }
    }

}