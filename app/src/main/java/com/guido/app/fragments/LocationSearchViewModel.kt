package com.guido.app.fragments

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.guido.app.MyApp
import com.guido.app.MyApp.Companion.nearByAttractions
import com.guido.app.data.places.PlacesRepository
import com.guido.app.model.MarkerData
import com.guido.app.model.placesUiModel.PlaceUiModel
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


    private val _currentFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val currentFormattedAddress: LiveData<String> get() = _currentFormattedAddress


    private val _searchedFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String> get() = _searchedFormattedAddress

    val markerDataList = mutableListOf<MarkerData>()


    fun fetchCurrentAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
            MyApp.userCurrentFormattedAddress = address
            _searchedFormattedAddress.postValue(address)
        }
    }

    private fun fetchSearchedLocationAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
            _searchedFormattedAddress.postValue(address)
        }
    }


    @SuppressLint("MissingPermission")
    fun fetchPlacesDetailsNearMe(
        location: String,
        radius: Int,
        type: String,
        keyword: String,
        key: String,
    ) {
        fetchSearchedLocationAddressFromGeoCoding(location,key)
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.getAllSavedPlaceTypePreferences().collect{
                val interestes =  it.map { it.id }.toString()
                nearByAttractions = placesRepository.fetchPlacesNearMe(
                    location, radius,type, interestes, key
                )
                _nearByPlaces.emit(nearByAttractions)
            }

        }
    }





}