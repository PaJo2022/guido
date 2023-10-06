package com.innoappsai.guido.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.innoappsai.guido.Constants.getPlaceTypeIcon
import com.innoappsai.guido.LocationClient
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.DataType
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiType
import com.innoappsai.guido.model.placesUiModel.addUiType
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

    private val _isLoading: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isLoading: SharedFlow<Boolean> get() = _isLoading


    private val _placeUiState: MutableLiveData<PlaceUiState> = MutableLiveData()
    val placeUiState: LiveData<PlaceUiState> get() = _placeUiState


    private val nearByMarkerList : ArrayList<LatLng?> = ArrayList()
    val markerDataList : ArrayList<MarkerData> = ArrayList()

    private val _searchedFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String> = _searchedFormattedAddress


    private val nearByPlacesListInGroup: ArrayList<PlaceTypeUiModel> = ArrayList()
    private val nearByPlacesList: ArrayList<PlaceUiModel> = ArrayList()
    private var listOfPlaces: ArrayList<PlaceUiModel> = ArrayList()

    private val _dataState: MutableSharedFlow<DataState> = MutableSharedFlow()
    val dataState: SharedFlow<DataState> get() = _dataState

    private fun fetchCurrentAddressFromGeoCoding(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(
                latitude, longitude
            )?.address.toString()
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
            lastSearchLocationLatLng!!.longitude
        )
    }



    fun fetchPlacesDetailsNearMe(
        latitude: Double,
        longitude: Double
    ) {
        val radius = appPrefs.prefDistance
        lastSearchLocationLatLng = LatLng(latitude, longitude)
        fetchCurrentAddressFromGeoCoding(latitude, longitude)
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.emit(true)
            _dataState.emit(DataState.LOADING)
            nearByPlacesListInGroup.clear()
            nearByPlacesList.clear()
            nearByMarkerList.clear()
            listOfPlaces.clear()
            val interestList = placesRepository.getAllSavedPlaceTypePreferences()
            val job2 = async {
                placesRepository.fetchPlacesNearMe(
                    latitude, longitude, radius, interestList.map { it.id }
                )
            }
            listOfPlaces = ArrayList(job2.await())
            val latLangs = listOfPlaces.map { it.latLng }

            val placesInGroupData = async { mapPlacesByType(listOfPlaces) }.await()
            nearByPlacesListInGroup.addAll(placesInGroupData)
            placesInGroupData.forEach {placeTypeUiModel->
                nearByPlacesList.addAll(ArrayList(placeTypeUiModel.places))
            }

            nearByMarkerList.addAll(latLangs)
            if (placesInGroupData.isEmpty()) {
                _dataState.emit(DataState.EMPTY_DATA)
            }
            _nearByPlacesInGroup.postValue(ArrayList(nearByPlacesListInGroup))
            _nearByPlaces.postValue(ArrayList(nearByPlacesList))
            _nearByPlacesMarkerPoints.postValue(ArrayList(nearByPlacesList))
            _isLoading.emit(false)

        }
    }

    fun removePlaceUsingPlaceId(placeId : String){
        viewModelScope.launch(Dispatchers.IO) {
            nearByPlacesListInGroup.clear()
            nearByPlacesList.clear()
            nearByMarkerList.clear()
            listOfPlaces.removeIf { it.placeId == placeId }
            val latLangs = listOfPlaces.map { it.latLng }
            val placesInGroupData = async { mapPlacesByType(listOfPlaces) }.await()
            nearByPlacesListInGroup.addAll(placesInGroupData)
            placesInGroupData.forEach {placeTypeUiModel->
                nearByPlacesList.addAll(ArrayList(placeTypeUiModel.places))
            }

            nearByMarkerList.addAll(latLangs)
            if (placesInGroupData.isEmpty()) {
                _dataState.emit(DataState.EMPTY_DATA)
            }
            _nearByPlacesInGroup.postValue(ArrayList(nearByPlacesListInGroup))
            _nearByPlaces.postValue(ArrayList(nearByPlacesList))
            _nearByPlacesMarkerPoints.postValue(ArrayList(nearByPlacesList))
        }
    }

    private suspend fun mapPlacesByType(
        places: List<PlaceUiModel>
    ): MutableList<PlaceTypeUiModel> {
        // Create a map to store places by type
        val placeUiTypeUiModel = mutableListOf<PlaceTypeUiModel>()
        val placesGroupedByType = places.groupBy { it.superType }
        placeUiTypeUiModel.clear()
        // Iterate through the place types
        placesGroupedByType.entries.forEach {mapData->


            placeUiTypeUiModel.add(
                PlaceTypeUiModel(
                    mapData.key,
                    getPlaceTypeIcon(mapData.key.toString()),
                    places = mapData.value.addUiType(PlaceUiType.LARGE),
                    dataType =  DataType.DATA
                )
            )


        }

        return placeUiTypeUiModel
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
                fetchPlacesDetailsNearMe(
                    latLng.latitude, latLng.longitude
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

    fun getUserData() = userRepository.getUserDetailsFlow()

    enum class PlaceUiState {
        HORIZONTAL, VERTICAL, NONE
    }

    enum class DataState{
        LOADING,EMPTY_DATA
    }


}