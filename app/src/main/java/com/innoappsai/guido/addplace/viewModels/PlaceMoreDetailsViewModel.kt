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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceMoreDetailsViewModel @Inject constructor(

) : ViewModel() {
    private val _placeFeatures: MutableLiveData<List<PlaceFeature>> = MutableLiveData()
    val placeFeatures: LiveData<List<PlaceFeature>> = _placeFeatures

    private val _placeTimings: MutableLiveData<List<PlaceTimings>> = MutableLiveData()
    val placeTimings: LiveData<List<PlaceTimings>> = _placeTimings


    private val _selectedDayOfTheWeek: MutableLiveData<String> = MutableLiveData()
    val selectedDayOfTheWeek: LiveData<String> = _selectedDayOfTheWeek

    private val _selectedWorkingHourTo: MutableLiveData<String> = MutableLiveData()
    val selectedWorkingHourTo: LiveData<String> = _selectedWorkingHourTo

    private val _selectedWorkingHourFrom: MutableLiveData<String> = MutableLiveData()
    val selectedWorkingHourFrom: LiveData<String> = _selectedWorkingHourFrom

    private val _placeFeaturesList = ArrayList(Constants.placeFeaturesList)
    private val _placeTimingList = ArrayList<PlaceTimings>()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

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
            if (_placeTimingList.size > 7) {
                _error.emit("Maximum 7 Timing Slots Can Be Selected")
                return@launch
            }
            val isThisAllReadyAdded =
                _placeTimingList.find { it.dayOfTheWeek == dayOfTheWeek || it.openingHour == from || it.closingHour == to } != null
            if (isThisAllReadyAdded) return@launch

            _placeTimingList.add(
                PlaceTimings(dayOfTheWeek = dayOfTheWeek, openingHour = from, closingHour = to)
            )

            _placeTimings.postValue(_placeTimingList)
        }
    }

    fun onPlaceTimingDeleted(dayOfTheWeek: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _placeTimingList.removeIf { it.dayOfTheWeek == dayOfTheWeek }
            _placeTimings.postValue(_placeTimingList)
        }
    }

    fun onDayOfTheWeekSelected(dayOfTheWeek: String) {
        _selectedDayOfTheWeek.value = dayOfTheWeek
    }

    fun onWorkingHoursFrom(hour: String) {
        _selectedWorkingHourFrom.value = hour
    }

    fun onWorkingHoursTo(hour: String) {
        _selectedWorkingHourTo.value = hour
    }

    fun getAllOpeningAndCloseTimings() = _placeTimingList
    fun getAllPlaceFeatures() = _placeFeaturesList.filter { it.isSelected }

}