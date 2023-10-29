package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private var totalCards = 0
    private var currentCard = 0
    fun fetchNearByPlace() {
        viewModelScope.launch(Dispatchers.IO) {
            val nearByPlaces = placesRepository.getPlacesNearMeFromLocalDb().first()
            totalCards = nearByPlaces.size
            _nearByPlacesState.postValue(nearByPlaces)
        }
    }



}