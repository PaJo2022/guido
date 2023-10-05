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
import com.innoappsai.guido.model.PlaceFeature
import com.innoappsai.guido.model.PlaceTimings
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

    private val _navigateNext: MutableSharedFlow<Unit> = MutableSharedFlow()
    val navigateNext: SharedFlow<Unit> = _navigateNext.asSharedFlow()

    private val _startAddingPlace: MutableSharedFlow<Pair<Array<String>, Array<String>>> =
        MutableSharedFlow()
    val startAddingPlace: SharedFlow<Pair<Array<String>, Array<String>>> =
        _startAddingPlace.asSharedFlow()

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

    private val _searchedFormattedAddress: MutableLiveData<String?> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String?> = _searchedFormattedAddress

    //Place DTO Data
    private var globalPlaceName: String? = null
    private var globalPlaceStreetAddress: String? = null
    private var globalPlaceCityName: String? = null
    private var globalPlaceStateName: String? = null
    private var globalPlaceCountryName: String? = null
    private var globalPlacePinCode: String? = null
    private var globalPlaceDescription: String? = null
    private var globalPlaceContactNumber: String? = null
    private var globalPlaceWebsite: String? = null
    private var globalPlaceInstagram: String? = null
    private var globalPlaceFacebook: String? = null
    private var globalPlaceBusinessEmail: String? = null
    private var globalPlaceBusinessOwner: String? = null
    private var globalPlaceBusinessSpecialNotes: String? = null
    private var globalPlaceFeatures: List<PlaceFeature>? = null
    private var globalPlaceAllTimings: ArrayList<PlaceTimings>? = null
    private var globalPlacePriceRange: String? = null
    private var globalPlaceType: String? = null
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
                _navigateNext.emit(Unit)
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
        placeCountryName: String,
        placePinCode: String
    ) {
        globalPlaceName = placeName
        globalPlaceStreetAddress = placeStreetAddress
        globalPlaceCityName = placeCityName
        globalPlaceStateName = placeStateName
        globalPlaceCountryName = placeCountryName
        globalPlacePinCode = placePinCode
        viewModelScope.launch {
            if (!listOf(
                    globalPlaceName,
                    globalPlaceStreetAddress,
                    globalPlaceCityName,
                    globalPlaceCountryName,
                    globalPlaceStateName
                ).any { it.isNullOrEmpty() }
            ) {
                _navigateNext.emit(Unit)
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }

    fun addSingleImageFileToList(fileUri : Uri) {
        viewModelScope.launch {
            if (imageFileArrayList.size < 5) {
                imageFileArrayList.add(fileUri)
                _placeImages.postValue(imageFileArrayList)
            } else {
                _error.emit("Max 10 Images Can Be Uploaded")
            }
        }
    }

    fun addImageFilesToList(fileUri: List<Uri>) {
        viewModelScope.launch {
            if (imageFileArrayList.size <= 5 && fileUri.size <= 5) {
                imageFileArrayList.addAll(fileUri)
                _placeImages.postValue(imageFileArrayList)
            } else {
                _error.emit("Max 5 Images Can Be Uploaded")
            }
        }
    }

    fun setPlaceBasicDetails(
        placeDescription: String,
        placeContactNumber: String,
        placePriceRange: String
    ) {
        globalPlaceDescription = placeDescription
        globalPlaceContactNumber = placeContactNumber
        globalPlacePriceRange = placePriceRange

        viewModelScope.launch {
            if (!listOf(
                    globalPlaceDescription,
                    globalPlaceContactNumber,
                    globalPlacePriceRange
                ).any { it.isNullOrEmpty() }
            ) {
                _navigateNext.emit(Unit)
            } else {
                _error.emit("Please Enter All The Details")
            }
        }
    }


    fun setMoreDetails(
        placeWebsite : String,
        placeInstagram: String,
        placeFacebook: String,
        placeBusinessEmail: String,
        placeOwner: String,
        placeSpecialNotes: String,
        placeOpeningCloseTimeList: ArrayList<PlaceTimings>,
        allPlaceFeatures: List<PlaceFeature>
    ) {
        globalPlaceWebsite = placeWebsite
        globalPlaceInstagram = placeInstagram
        globalPlaceFacebook = placeFacebook
        globalPlaceBusinessEmail = placeBusinessEmail
        globalPlaceBusinessOwner = placeOwner
        globalPlaceBusinessSpecialNotes = placeSpecialNotes
        globalPlaceBusinessSpecialNotes = placeSpecialNotes
        globalPlaceAllTimings = placeOpeningCloseTimeList
        globalPlaceFeatures = allPlaceFeatures
        viewModelScope.launch {
            _navigateNext.emit(Unit)
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
            val job = async {
                placesRepository.fetchAddressFromLatLng(
                    latitude, longitude
                )
            }
            val fullPlaceData = job.await()
            globalPlaceStreetAddress = fullPlaceData?.address.toString()
            globalPlaceCityName = fullPlaceData?.cityOrVillage.toString()
            globalPlaceStateName = fullPlaceData?.state.toString()
            globalPlaceCountryName = fullPlaceData?.country.toString()
            MyApp.userCurrentFormattedAddress = fullPlaceData?.address.toString()
            _searchedFormattedAddress.postValue(fullPlaceData?.address.toString())
            _isLoading.emit(false)
        }
    }

    fun getStreetAddress() = globalPlaceStreetAddress
    fun getCityName() = globalPlaceCityName
    fun getStateName() = globalPlaceStateName
    fun getCountryName() = globalPlaceCountryName
    fun getLatLong() = Pair(globalPlaceLatitude,globalPlaceLongitude)
    fun onGoogleMapMoving() {
        viewModelScope.launch {
            _isLoading.emit(true)
        }
    }

    fun addVideoUris(fileUri: List<Uri>) {
        viewModelScope.launch {
            if (videoFileArrayList.size <= 3 && fileUri.size <= 3) {
                videoFileArrayList.addAll(fileUri)
                _placeVideos.postValue(videoFileArrayList)
            } else {
                _error.emit("Max 3 Videos Can Be Uploaded")
            }
        }
    }

    fun uploadPlaceData() {
        viewModelScope.launch {
            val imageUriArray = imageFileArrayList.map { it.toString() }.toTypedArray()
            val videoUriArray = videoFileArrayList.map { it.toString() }.toTypedArray()
            placeRequestDTO = PlaceRequestDTO(
                contactNumber = globalPlaceContactNumber,
                createdBy = appPrefs.userId,
                location = PlaceRequestLocation(
                    coordinates = listOf(
                        globalPlaceLongitude ?: 0.0,
                        globalPlaceLatitude ?: 0.0
                    )
                ),
                photos = null,
                videos = null,
                placeAddress = globalPlaceStreetAddress,
                placeCity = globalPlaceCityName,
                placeState = globalPlaceStateName,
                placeCountry = globalPlaceCountryName,
                placeDescription = globalPlaceDescription,
                pricingType = globalPlacePriceRange,
                placeId = UUID.randomUUID().toString(),
                rating = 0.0,
                placeName = globalPlaceName,
                types = listOf(globalPlaceType ?: ""),
                website = globalPlaceWebsite,
                instagram = globalPlaceInstagram,
                facebook = globalPlaceFacebook,
                businessEmail = globalPlaceBusinessEmail,
                businessOwner = globalPlaceBusinessOwner,
                businessSpecialNotes = globalPlaceBusinessSpecialNotes,
                placeFeatures = globalPlaceFeatures?.map { it.featureName },
                placeTimings = globalPlaceAllTimings
            )
            MyApp.placeRequestDTO = placeRequestDTO
            _startAddingPlace.emit(Pair(imageUriArray, videoUriArray))
        }
    }


    sealed class PlaceAddScreenName {
        object ADD_TYPES : PlaceAddScreenName()
        object ADD_ADDRESS : PlaceAddScreenName()
        object ADD_DETAILS : PlaceAddScreenName()
        object ADD_SOCIAL_DETAILS : PlaceAddScreenName()
        object ADD_IMAGE_VIDEOS : PlaceAddScreenName()
        data class COMPLETE(
            val placeRequestDTO: PlaceRequestDTO,
            val imageUri: Array<String>,
            val videoUri: Array<String>
        ) : PlaceAddScreenName()

    }

}