package com.innoappsai.guido.addplace.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.Constants
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.PlaceTypeContainer
import com.innoappsai.guido.model.places_backend_dto.Location
import com.innoappsai.guido.model.places_backend_dto.PlaceDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestDTO
import com.innoappsai.guido.model.places_backend_dto.PlaceRequestLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddPlaceViewModel @Inject constructor(
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _placeTypes: MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()
    val placeTypes: LiveData<List<PlaceTypeContainer>> = _placeTypes

    private val _currentScreenName: MutableSharedFlow<PlaceAddScreenName> = MutableSharedFlow()
    val currentScreenName: SharedFlow<PlaceAddScreenName> = _currentScreenName.asSharedFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val imageFileArrayList: ArrayList<ByteArray> = ArrayList()

    private val _placeImages: MutableLiveData<ArrayList<ByteArray>> = MutableLiveData()
    val placeImages: LiveData<ArrayList<ByteArray>> = _placeImages

    //Place DTO Data
    private var globalPlaceName: String = ""
    private var globalPlaceStreetAddress: String = ""
    private var globalPlaceCityName: String = ""
    private var globalPlaceStateName: String = ""
    private var globalPlacePinCode: String = ""
    private var globalPlaceDescription: String = ""
    private var globalPlaceContactNumber: String = ""
    private var globalPlaceWebsite: String = ""
    private var globalPlacePriceRange: String = ""
    private var globalPlaceType: String = ""

    private var placeRequestDTO: PlaceRequestDTO? = null

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
                globalPlaceType = selectedPlaceType.id
                _currentScreenName.emit(PlaceAddScreenName.ADD_ADDRESS)
            } else {
                _error.emit("Please Select You Place Type")
            }
        }
    }

    fun setPlaceAddressDetails(
        placeName: String,
        placeStreetAddress: String,
        placeCityName: String,
        placeStateName: String,
        placePinCode: String
    ) {
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
                ).any { it.isNullOrEmpty() }
            ) {
                _currentScreenName.emit(PlaceAddScreenName.ADD_DETAILS)
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }

    fun addImageFilesToList(fileArray: ByteArray) {
        viewModelScope.launch {
            if (imageFileArrayList.size <= 10) {
                imageFileArrayList.add(fileArray)
                _placeImages.postValue(imageFileArrayList)
            } else {
                _error.emit("Max 10 Images Can Be Uploaded")
            }
        }
    }

    fun setPlaceDetails(
        placeDescription: String,
        placeContactNumber: String,
        placeWebsite: String,
        placePriceRange: String
    ) {
        globalPlaceDescription = placeDescription
        globalPlaceContactNumber = placeContactNumber
        globalPlaceWebsite = placeWebsite
        globalPlacePriceRange = placePriceRange
        viewModelScope.launch {
            if (!listOf(
                    globalPlaceDescription,
                    globalPlaceContactNumber,
                    globalPlacePriceRange
                ).any { it.isNullOrEmpty() }
            ) {
                placeRequestDTO = PlaceRequestDTO(
                    contactNumber = globalPlaceContactNumber,
                    website = globalPlaceWebsite,
                    createdBy = appPrefs.userId,
                    location = PlaceRequestLocation(coordinates = listOf(MyApp.userCurrentLatLng?.longitude?: 0.0,MyApp.userCurrentLatLng?.latitude ?: 0.0)),
                    placeAddress = globalPlaceStreetAddress,
                    placeName = globalPlaceName,
                    placeId = UUID.randomUUID().toString(),
                    photos = null,
                    types = listOf(globalPlaceType)
                )
                _currentScreenName.emit(PlaceAddScreenName.COMPLETE(placeRequestDTO!!))
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }


    sealed class PlaceAddScreenName {
        object ADD_TYPES : PlaceAddScreenName()
        object ADD_ADDRESS : PlaceAddScreenName()
        object ADD_DETAILS : PlaceAddScreenName()
        data class COMPLETE(val placeRequestDTO: PlaceRequestDTO) : PlaceAddScreenName()

    }

}