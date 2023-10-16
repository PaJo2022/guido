package com.innoappsai.guido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.places.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val placesRepository: PlacesRepository) :
    ViewModel() {

    fun removeAllSavedPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.deleteAllPlacesFromDB()
        }
    }
}