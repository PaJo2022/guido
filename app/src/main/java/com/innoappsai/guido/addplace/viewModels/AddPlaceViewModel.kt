package com.innoappsai.guido.addplace.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.PlaceTypeContainer
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPlaceViewModel @Inject constructor() : ViewModel() {

    private val _placeTypes: MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()
    val placeTypes: LiveData<List<PlaceTypeContainer>> = _placeTypes

    private val _currentScreenName: MutableSharedFlow<PlaceAddScreenName> = MutableSharedFlow()
    val currentScreenName: SharedFlow<PlaceAddScreenName> = _currentScreenName.asSharedFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    //Place DTO Data
    private var globalPlaceName: String = ""
    private var globalPlaceStreetAddress: String = ""
    private var globalPlaceCityName: String = ""
    private var globalPlaceStateName: String = ""
    private var globalPlacePinCode: String = ""

    private val placeTypesList = ArrayList(Constants.placeTypes)


    fun getAllPlaceTypes() {
        viewModelScope.launch(Dispatchers.IO) {
            val job = async {
                val groupedPlaceTypes = placeTypesList.groupBy { it.type }

                groupedPlaceTypes.map { (type, placeTypeList) ->
                    PlaceTypeContainer(
                        type,
                        placeTypeList,
                        placeTypeList.find { it.isSelected } != null)
                }
            }
            _placeTypes.postValue(job.await())
        }

    }

    fun onPlaceTypeSelected(placeType: PlaceType) {
        viewModelScope.launch(Dispatchers.IO) {
            val job1 = async {
                placeTypesList.forEach {
                    it.isSelected = it.id == placeType.id
                }
            }
            job1.await()
            val jo2 = async {
                val groupedPlaceTypes = placeTypesList.groupBy { it.type }

                groupedPlaceTypes.map { (type, placeTypeList) ->
                    PlaceTypeContainer(
                        type,
                        placeTypeList,
                        placeTypeList.find { it.isSelected } != null)
                }
            }
            _placeTypes.postValue(jo2.await())
        }
    }

    fun goToAddPlaceDetails() {
        viewModelScope.launch {
            val selectedPlaceType = placeTypesList.find { it.isSelected }
            if (selectedPlaceType != null) {
                _currentScreenName.emit(PlaceAddScreenName.ADD_ADDRESS)
            } else {
                _error.emit("Please Select You Place Type")
            }
        }
    }

    fun setPlaceDetails(placeName: String, placeStreetAddress: String, placeCityName: String, placeStateName: String, placePinCode: String) {
        globalPlaceName = placeName
        globalPlaceStreetAddress = placeStreetAddress
        globalPlaceCityName = placeCityName
        globalPlaceStateName = placeStateName
        globalPlacePinCode = placePinCode
        viewModelScope.launch {
            if (!listOf(
                    globalPlaceName,
                    globalPlaceStreetAddress,
                    globalPlaceCityName,
                    globalPlaceStateName
                ).any { it.isNullOrEmpty() }) {
                _currentScreenName.emit(PlaceAddScreenName.ADD_DETAILS)
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }


    enum class PlaceAddScreenName {
        ADD_TYPES, ADD_ADDRESS, ADD_DETAILS
    }

}