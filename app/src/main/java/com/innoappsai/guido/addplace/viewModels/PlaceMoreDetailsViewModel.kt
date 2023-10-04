package com.innoappsai.guido.addplace.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.model.PlaceFeature
import com.innoappsai.guido.model.PlaceTimings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceMoreDetailsViewModel @Inject constructor(

) : ViewModel() {
    private val _placeFeatures: MutableLiveData<List<PlaceFeature>> = MutableLiveData()
    val placeFeatures: LiveData<List<PlaceFeature>> = _placeFeatures

    private val _placeTimings: MutableLiveData<List<PlaceTimings>> = MutableLiveData()
    val placeTimings: LiveData<List<PlaceTimings>> = _placeTimings

    private val _placeFeaturesList = ArrayList(Constants.placeFeaturesList)
    private val _placeTimingList = ArrayList<PlaceTimings>()

    init {
        _placeFeatures.value = _placeFeaturesList
    }

    fun onPlaceFeatureClicked(placeFeature: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSelected =
                _placeFeaturesList.find { it.featureName == placeFeature }?.isSelected ?: false
            _placeFeaturesList.find { it.featureName == placeFeature }?.isSelected = !isSelected
            _placeFeatures.postValue(_placeFeaturesList)
        }
    }


    fun onPlaceTimingAdded(dayOfTheWeek: String, from: String, to: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isThisAllReadyAdded =
                _placeTimingList.find { it.dayOfTheWeek == dayOfTheWeek && it.from == from && it.to == to } != null
            if (isThisAllReadyAdded) return@launch

            _placeTimingList.add(
                PlaceTimings(dayOfTheWeek = dayOfTheWeek, from = from, to = to)
            )

            _placeTimings.postValue(_placeTimingList)
        }
    }

    fun onPlaceTimingDeleted(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _placeTimingList.removeIf { it.id == id }
            _placeTimings.postValue(_placeTimingList)
        }
    }

}