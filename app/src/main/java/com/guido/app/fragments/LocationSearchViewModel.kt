package com.guido.app.fragments

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.guido.app.data.places.PlacesRepository
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationSearchViewModel @Inject constructor(private val placesRepository: PlacesRepository) :
    ViewModel() {

    private val _nearByPlaces: MutableStateFlow<List<PlaceUiModel>> = MutableStateFlow(emptyList())
    val nearByPlaces: StateFlow<List<PlaceUiModel>> get() = _nearByPlaces.asStateFlow()



    fun getSelectedPreferences(){

    }



    @SuppressLint("MissingPermission")
    fun fetchPlacesDetailsNearMe(
        location : String,
        radius : Int,
        type : String,
        keyword : String,
        key : String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.getAllSavedPlaceTypePreferences().collect{
                var interestes =  it.map { it.id }.toString()

                Log.i("JAPAN", "fetchPlacesDetailsNearMe: ${interestes}")
                val nearByAttractions = placesRepository.fetchPlacesNearMe(
                    location, radius,type, interestes, key
                )
                _nearByPlaces.emit(nearByAttractions)
            }

        }
    }



}