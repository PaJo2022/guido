package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelChooseNearByPlaces @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {
    private val _nearByPlacesState: MutableLiveData<List<PlaceUiModel>> =
        MutableLiveData()
    val nearByPlacesState: LiveData<List<PlaceUiModel>> get() = _nearByPlacesState


    private val _selectedTypePlacesNearLocation: MutableLiveData<List<PlaceUiModel>> =
        MutableLiveData()
    val selectedTypePlacesNearLocation: LiveData<List<PlaceUiModel>> get() = _selectedTypePlacesNearLocation

    private var nearByPlaces: List<PlaceUiModel> = emptyList()
    fun fetchNearByPlace(selectedPlaceTypes : List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val latLng = MyApp.userCurrentLatLng ?: return@launch
            nearByPlaces = placesRepository.fetchPlacesNearLocation(
                latLng.latitude,
                latLng.longitude,
                25000,
                selectedPlaceTypes
            ).filter { it.photos?.isNotEmpty() == true }

            _selectedTypePlacesNearLocation.postValue(nearByPlaces)
        }
    }



}