package com.innoappsai.guido.addplace.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.Constants
import com.innoappsai.guido.LocationClient
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.PlaceTypeContainer
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
    private val appPrefs: AppPrefs,
    private val locationClient: LocationClient,
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _placeTypes: MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()
    val placeTypes: LiveData<List<PlaceTypeContainer>> = _placeTypes

    private val _currentScreenName: MutableSharedFlow<PlaceAddScreenName> = MutableSharedFlow()
    val currentScreenName: SharedFlow<PlaceAddScreenName> = _currentScreenName.asSharedFlow()

    private val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error.asSharedFlow()

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> = _isLoading.asSharedFlow()

    private val imageFileArrayList: ArrayList<Uri> = ArrayList()
    private val videoFileArrayList: ArrayList<Uri> = ArrayList()

    private val _placeImages: MutableLiveData<ArrayList<Uri>> = MutableLiveData()
    val placeImages: LiveData<ArrayList<Uri>> = _placeImages

    private val _placeVideos: MutableLiveData<ArrayList<Uri>> = MutableLiveData()
    val placeVideos: LiveData<ArrayList<Uri>> = _placeVideos

    private val _moveToLocation: MutableLiveData<Pair<LatLng, Boolean>> = MutableLiveData()
    val moveToLocation: LiveData<Pair<LatLng, Boolean>> get() = _moveToLocation

    private val _searchedFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String> = _searchedFormattedAddress

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
    private var globalPlaceLatitude: Double? = null
    private var globalPlaceLongitude: Double? = null

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

    fun addImageFilesToList(fileUri : Uri) {
        viewModelScope.launch {
            if (imageFileArrayList.size <= 10) {
                imageFileArrayList.add(fileUri)
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
                ).any { it.isEmpty() }
            ) {
                placeRequestDTO = PlaceRequestDTO(
                    contactNumber = globalPlaceContactNumber,
                    website = globalPlaceWebsite,
                    createdBy = appPrefs.userId,
                    location = PlaceRequestLocation(
                        coordinates = listOf(
                            globalPlaceLongitude ?: 0.0,
                            globalPlaceLatitude ?: 0.0
                        )
                    ),
                    placeDescription = globalPlaceDescription,
                    placeAddress = globalPlaceStreetAddress,
                    placeName = globalPlaceName,
                    placeId = UUID.randomUUID().toString(),
                    rating = 0.0,
                    photos = null,
                    pricingType = globalPlacePriceRange,
                    videos = emptyList(),
                    types = listOf(globalPlaceType)
                )
                val imageUriArray = imageFileArrayList.map { it.toString() }.toTypedArray()
                val videoUriArray = videoFileArrayList.map { it.toString() }.toTypedArray()
                _currentScreenName.emit(PlaceAddScreenName.COMPLETE(placeRequestDTO!!,imageUriArray,videoUriArray))
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }

    fun fetchCurrentLocation(shouldAnimate: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocation = locationClient.getCurrentLocation()
            MyApp.userCurrentLatLng = currentLocation
            currentLocation?.let { latLng ->
                _moveToLocation.postValue(Pair(latLng, true))
                fetchCurrentAddressFromGeoCoding(
                    latLng.latitude, latLng.longitude
                )
            }
        }
    }

    fun fetchCurrentAddressFromGeoCoding(
        latitude: Double,
        longitude: Double,
    ) {
        globalPlaceLatitude = latitude
        globalPlaceLongitude = longitude
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            val address = placesRepository.fetchAddressFromLatLng(
                latitude, longitude
            )?.display_name.toString()
            globalPlaceStreetAddress = address
            MyApp.userCurrentFormattedAddress = address
            _searchedFormattedAddress.postValue(address)
            _isLoading.emit(false)
        }
    }

    fun getStreetAddress() = globalPlaceStreetAddress
    fun onGoogleMapMoving() {
        viewModelScope.launch {
            _isLoading.emit(true)
        }
    }

    fun addVideoUris(fileUri: List<Uri>) {
        viewModelScope.launch {
            if (videoFileArrayList.size <= 10) {
                videoFileArrayList.addAll(fileUri)
                _placeVideos.postValue(videoFileArrayList)
            } else {
                _error.emit("Max 3 Videos Can Be Uploaded")
            }
        }
    }


    sealed class PlaceAddScreenName {
        object ADD_TYPES : PlaceAddScreenName()
        object ADD_ADDRESS : PlaceAddScreenName()
        object ADD_DETAILS : PlaceAddScreenName()
        data class COMPLETE(val placeRequestDTO: PlaceRequestDTO,val imageUri : Array<String>,val videoUri :Array<String>) : PlaceAddScreenName()

    }

}