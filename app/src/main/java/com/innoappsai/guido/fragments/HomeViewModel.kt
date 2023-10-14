package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.innoappsai.guido.Constants.getPlaceTypeIcon
import com.innoappsai.guido.LocationClient
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.DataType
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiType
import com.innoappsai.guido.model.placesUiModel.addUiType
import com.innoappsai.guido.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

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


    private val nearByPlacesList: ArrayList<PlaceUiModel> = ArrayList()


    private val _dataState: MutableSharedFlow<DataState> = MutableSharedFlow()
    val dataState: SharedFlow<DataState> get() = _dataState

    private val _showItineraryGenerationLayout: MutableLiveData<Boolean> = MutableLiveData()
    val showItineraryGenerationLayout: LiveData<Boolean> get() = _showItineraryGenerationLayout


    private val _selectedPlaces: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val selectedPlaces: LiveData<List<PlaceUiModel>> get() = _selectedPlaces


    init {
        getNearByPlaces()
    }

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
            _nearByPlacesInGroup.postValue(DUMMY_PLACE_TYPE_UI_MODEL)
            val interestList =
                if (MyApp.tempPlaceInterestes.isNullOrEmpty()) placesRepository.getAllSavedPlaceTypePreferences() else emptyList()
            val response = placesRepository.fetchPlacesNearMeAndSaveInLocalDb(
                latitude,
                longitude,
                if (MyApp.tempPlaceDistance != null) MyApp.tempPlaceDistance!! else radius,
                if (MyApp.tempPlaceInterestes != null) MyApp.tempPlaceInterestes!!.map { it.id } else interestList.map { it.id }
            )
            if (response is Resource.Error) {
                _dataState.emit(DataState.EMPTY_DATA)
                _nearByPlacesInGroup.postValue(emptyList())
                _nearByPlaces.postValue(ArrayList(emptyList()))
                _nearByPlacesMarkerPoints.postValue(emptyList())
                _isLoading.emit(false)
            }else if (response is Resource.Success){
                if(response.data.isNullOrEmpty()){
                    _dataState.emit(DataState.EMPTY_DATA)
                    _nearByPlacesInGroup.postValue(emptyList())
                    _nearByPlaces.postValue(ArrayList(emptyList()))
                    _nearByPlacesMarkerPoints.postValue(emptyList())
                    _isLoading.emit(false)
                }
            }
        }

    }

    private fun getNearByPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.getPlacesNearMeFromLocalDb().map { ArrayList(it) }.collect{listOfPlaceUiModel->
                nearByPlacesList.clear()
                nearByMarkerList.clear()

                val latLangs = listOfPlaceUiModel.map { it.latLng }

                val placesInGroupData = mapPlacesByType(listOfPlaceUiModel)
                placesInGroupData.forEach { placeTypeUiModel ->
                    placeTypeUiModel.places?.let { nearByPlacesList.addAll(it) }
                }
                val nearByPlacesListInGroup = placesInGroupData.map {
                    PlaceTypeUiModel(
                        type = it.type,
                        icon = it.icon,
                        places = it.places,
                        dataType = it.dataType
                    )
                }

                nearByMarkerList.addAll(latLangs)
                if (listOfPlaceUiModel.isEmpty()) {
                    _dataState.emit(DataState.EMPTY_DATA)
                    _nearByPlacesInGroup.postValue(emptyList())
                    _nearByPlaces.postValue(ArrayList(emptyList()))
                    _nearByPlacesMarkerPoints.postValue(emptyList())
                    _isLoading.emit(false)
                } else {
                    _nearByPlacesInGroup.postValue(nearByPlacesListInGroup)
                    _nearByPlaces.postValue(listOfPlaceUiModel)
                    _nearByPlacesMarkerPoints.postValue(listOfPlaceUiModel)
                    _isLoading.emit(false)
                    _selectedPlaces.postValue(listOfPlaceUiModel.filter { it.isChecked })
                }

            }
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
            val sortedPlaces = mapData.value.sortedBy { it.rating }

            placeUiTypeUiModel.add(
                PlaceTypeUiModel(
                    mapData.key,
                    getPlaceTypeIcon(mapData.key.toString()),
                    places = sortedPlaces.addUiType(PlaceUiType.LARGE),
                    dataType =  DataType.DATA
                )
            )


        }

        return placeUiTypeUiModel
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
            //_nearByPlaces.postValue(markerDataList.map { it.placeUiModel })
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
    fun deletePlace(placeId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.deletePlaceFromDB(placeId.toString())
        }
    }

    fun onItineraryGenerationClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _showItineraryGenerationLayout.postValue(true)
            placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(true,true)
        }
    }

    fun onItineraryGenerationCancelledClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _showItineraryGenerationLayout.postValue(false)
            placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(false,false)
        }
    }

    fun onPlaceSelectedForItinerary(placeId: String?, checked: Boolean) {
        if(placeId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.updatePlaceIsChecked(placeId,checked)
        }
    }

    enum class PlaceUiState {
        HORIZONTAL, VERTICAL, NONE
    }

    enum class DataState {
        LOADING, EMPTY_DATA
    }


}