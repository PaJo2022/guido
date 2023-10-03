package com.innoappsai.guido.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomPlaceOptionsViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val appPrefs: AppPrefs
) : ViewModel() {

    var placeData: PlaceUiModel? = null

    private val _onPlaceDeleted: MutableSharedFlow<String> = MutableSharedFlow()
    val onPlaceDeleted: SharedFlow<String> = _onPlaceDeleted.asSharedFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()


    fun deletePlaceById(placeId: String) {
        val userId = appPrefs.userId ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val deletedPlace = placesRepository.deletePlaceById(userId, placeId)
            if (deletedPlace != null) {
                _onPlaceDeleted.emit(deletedPlace.name.toString())
            } else {
                _error.emit("Something went wrong")
            }
        }
    }
}