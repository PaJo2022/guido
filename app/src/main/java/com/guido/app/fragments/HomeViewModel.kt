package com.guido.app.fragments

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.guido.app.LocationClient
import com.guido.app.MyApp
import com.guido.app.R
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.MarkerData
import com.guido.app.model.PlaceType
import com.guido.app.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiType
import com.guido.app.model.placesUiModel.addUiType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.Arrays
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val placesClient: PlacesClient,
    private val appPrefs: AppPrefs,
    private var locationClient: LocationClient,
    private val userRepository: UserRepository
) :
    ViewModel() {

    var bottomsheetPlaceListLastPosition = 0
    var lastSearchLocationLatLng: LatLng? = null

    private val _nearByPlacesInGroup: MutableLiveData<List<PlaceTypeUiModel>> =
        MutableLiveData()
    val nearByPlacesInGroup: LiveData<List<PlaceTypeUiModel>> get() = _nearByPlacesInGroup

    private val _nearByPlaces: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val nearByPlaces: LiveData<List<PlaceUiModel>> get() = _nearByPlaces


    private val _nearByPlacesMarkerPoints: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val nearByPlacesMarkerPoints: MutableLiveData<List<PlaceUiModel>> get() = _nearByPlacesMarkerPoints


    private val _scrollHorizontalPlaceListToPosition: MutableSharedFlow<Int> = MutableSharedFlow()
    val scrollHorizontalPlaceListToPosition: SharedFlow<Int> get() = _scrollHorizontalPlaceListToPosition

    private val _selectedMarker: MutableSharedFlow<MarkerData> = MutableSharedFlow()
    val selectedMarker: SharedFlow<MarkerData> get() = _selectedMarker

    private val _moveToLocation: MutableLiveData<Pair<LatLng, Boolean>> = MutableLiveData()
    val moveToLocation: LiveData<Pair<LatLng, Boolean>> get() = _moveToLocation

    private val _currentLatLng: MutableLiveData<LatLng> = MutableLiveData()
    val currentLatLng: LiveData<LatLng> get() = _currentLatLng


    private val _placeUiState: MutableLiveData<PlaceUiState> = MutableLiveData()
    val placeUiState: LiveData<PlaceUiState> get() = _placeUiState


    private val nearByMarkerList : ArrayList<LatLng?> = ArrayList()
    val markerDataList : ArrayList<MarkerData> = ArrayList()

    private val _searchedFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String> = _searchedFormattedAddress


    private val nearByPlacesListInGroup: ArrayList<PlaceTypeUiModel> = ArrayList()
    private val nearByPlacesList: ArrayList<PlaceUiModel> = ArrayList()

    private val _dataState: MutableSharedFlow<DataState> = MutableSharedFlow()
    val dataState: SharedFlow<DataState> get() = _dataState

    fun fetchCurrentAddressFromGeoCoding(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(
                latitude, longitude
            )?.display_name.toString()
            MyApp.userCurrentFormattedAddress = address
            _searchedFormattedAddress.postValue(address)
        }
    }

    fun moveToTheLatLng(latLng: LatLng) {
        _moveToLocation.value = Pair(latLng, true)
    }


    fun resetData() {
        nearByPlacesListInGroup.clear()
        nearByPlacesList.clear()
        nearByMarkerList.clear()
        _nearByPlacesInGroup.value = DUMMY_PLACE_TYPE_UI_MODEL
        _nearByPlaces.value = emptyList()
        _nearByPlacesMarkerPoints.value = emptyList()
    }

    fun resetSearchWithNewInterestes() {
        if (lastSearchLocationLatLng == null) return
        fetchPlacesDetailsNearMe(
            lastSearchLocationLatLng!!.latitude,
            lastSearchLocationLatLng!!.longitude,
            appPrefs.prefDistance
        )
    }

    private fun formatInterestString(input: String): String {
        val lowercaseInput = input.lowercase()
        val words = lowercaseInput.split(" ")
        val formattedString = words.joinToString("_")
        return if (words.size == 1) lowercaseInput else formattedString
    }


    @SuppressLint("MissingPermission")
    fun fetchPlacesDetailsNearMe(
        latitude: Double,
        longitude: Double,
        radius: Int
    ) {
        fetchCurrentAddressFromGeoCoding(latitude, longitude)
        viewModelScope.launch(Dispatchers.IO) {
            _dataState.emit(DataState.LOADING)
            nearByPlacesListInGroup.clear()
            nearByPlacesList.clear()
            nearByMarkerList.clear()
            val interestList = placesRepository.getAllSavedPlaceTypePreferences()
            val job2 = async {
                placesRepository.fetchPlacesNearMe(
                    latitude, longitude, radius, interestList.map { it.id }
                )
            }
            val attraction = job2.await()
            val latLangs = attraction.map { it.latLng }
            val placeTypeUiModel = attraction.map { placeType ->
                PlaceTypeUiModel(
                    "FIX ME HERE",
                    placeType.iconDrawable,
                    attraction.addUiType(placeType.iconDrawable, PlaceUiType.LARGE),
                )
            }

            attraction.forEach {
                it.placeUiType
            }

            if (latLangs.isNotEmpty()) {
                nearByPlacesListInGroup.addAll(placeTypeUiModel)
                nearByPlacesList.addAll(
                    attraction.addUiType(
                        R.drawable.icon_department_store,
                        PlaceUiType.LARGE
                    )
                )
            }
            nearByMarkerList.addAll(latLangs)
            if (nearByPlacesListInGroup.isEmpty()) {
                _dataState.emit(DataState.EMPTY_DATA)
            }
            _nearByPlacesInGroup.postValue(ArrayList(nearByPlacesListInGroup))
            _nearByPlaces.postValue(ArrayList(nearByPlacesList))
            _nearByPlacesMarkerPoints.postValue(ArrayList(nearByPlacesList))

        }
    }


    private fun concatenatePlaceTypeIds(placeTypes: List<PlaceType>): String {
        val ids = placeTypes.map { it.id }
        return ids.joinToString("|")
    }



    fun fetchPlaceDetailsById(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
            val place = awaitPlaceDetailsConnection(request) ?: return@launch
            lastSearchLocationLatLng = place.latLng
            place.latLng?.let {
                _moveToLocation.postValue(Pair(it, true))
                fetchPlacesDetailsNearMe(
                    it.latitude, it.longitude,
                    appPrefs.prefDistance
                )
            }

        }
    }

    private suspend fun awaitPlaceDetailsConnection(request: FetchPlaceRequest): Place? {
        return suspendCoroutine {
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                it.resume(place)

            }.addOnFailureListener { exception ->
                it.resume(null)
            }
        }
    }


    fun fetchCurrentLocation(shouldAnimate: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocation = locationClient.getCurrentLocation()
            lastSearchLocationLatLng = currentLocation
            MyApp.userCurrentLatLng = currentLocation
            currentLocation?.let { latLng ->
                _moveToLocation.postValue(Pair(latLng, shouldAnimate))
                _currentLatLng.postValue(latLng)
                fetchPlacesDetailsNearMe(
                    latLng.latitude, latLng.longitude,
                    appPrefs.prefDistance
                )
                fetchCurrentAddressFromGeoCoding(
                    latLng.latitude, latLng.longitude
                )
            }
        }
    }


    fun showHorizontalUi(){
        _placeUiState.value = PlaceUiState.HORIZONTAL
    }

    fun showVerticalUi() {
        _placeUiState.value = PlaceUiState.VERTICAL
    }

    fun showNone() {
        _placeUiState.value = PlaceUiState.NONE
    }

    fun onMarkerClicked(id: String) {
        viewModelScope.launch {
            var selectedPosition = 0
            val job = async {
                markerDataList.forEachIndexed { index, it ->
                    val isItemFound = it.marker.id == id
                    it.placeUiModel.isSelected = isItemFound
                    if (isItemFound) {
                        selectedPosition = index
                    }
                }
            }
            job.await()
            _nearByPlaces.postValue(markerDataList.map { it.placeUiModel })
            _selectedMarker.emit(markerDataList[selectedPosition])
            _scrollHorizontalPlaceListToPosition.emit(selectedPosition)
        }
    }

    fun setThePositionForHorizontalPlaceAdapter(pos: Int) {
        viewModelScope.launch {
            _selectedMarker.emit(markerDataList[pos])
        }
    }

    fun getUserData() = userRepository.getUserDetailsFlow(appPrefs.userId.toString())

    enum class PlaceUiState {
        HORIZONTAL, VERTICAL, NONE
    }

    enum class DataState{
        LOADING,EMPTY_DATA
    }


}