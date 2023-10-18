package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.LocationClient
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.auth.repo.user.UserRepository
import com.innoappsai.guido.calculateDistance
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.PlaceFilter.PlaceFilter
import com.innoappsai.guido.model.PlaceFilter.PlaceFilterType
import com.innoappsai.guido.model.PlaceFilter.placeFiltersList
import com.innoappsai.guido.model.SortType
import com.innoappsai.guido.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
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


    private val _dataState: MutableLiveData<DataState> = MutableLiveData()
    val dataState: LiveData<DataState> get() = _dataState

    private val _showItineraryGenerationLayout: MutableLiveData<Boolean> = MutableLiveData()
    val showItineraryGenerationLayout: LiveData<Boolean> get() = _showItineraryGenerationLayout


    private val _selectedPlaces: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val selectedPlaces: LiveData<List<PlaceUiModel>> get() = _selectedPlaces

    private val _selectedFilters: MutableLiveData<ArrayList<PlaceFilter>> = MutableLiveData()
    val selectedFilters: LiveData<ArrayList<PlaceFilter>> get() = _selectedFilters

    private var isNewDataFetched: Boolean = false
    var isPlaceGeneratedOptionClicked = false

    private var sortType: SortType = SortType.DISTANCE
    private var filterOptions = ArrayList(placeFiltersList)


    init {
        getNearByPlaces()
    }

    fun toggleHyperLocalPlaceSearchOnPlaceFilterMenu(isEnabled: Boolean) {
        filterOptions.find { it.placeFilterType == PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH }?.isSelected =
            isEnabled
        _selectedFilters.value = filterOptions
    }

    fun onFilterOptionClicked(placeFilterType: PlaceFilterType) {
        viewModelScope.launch(Dispatchers.IO) {
            if (placeFilterType == PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH) {
                filterOptions.find { it.placeFilterType == placeFilterType }?.isSelected =
                    !filterOptions.find { it.placeFilterType == placeFilterType }?.isSelected!!
            } else {
                filterOptions.forEach {
                    if (placeFilterType != PlaceFilterType.MORE_FILTERS && placeFilterType != PlaceFilterType.FULL_FILTER && placeFilterType != PlaceFilterType.TRAVEL_ITINERARY) {
                        it.isSelected = it.placeFilterType.ordinal == placeFilterType.ordinal
                    }

                }
            }

            _selectedFilters.postValue(filterOptions)
        }
    }

    private fun fetchCurrentAddressFromGeoCoding(
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val fullPlaceData = placesRepository.fetchAddressFromLatLng(
                latitude, longitude
            )
            MyApp.userCurrentFormattedAddress = fullPlaceData?.address.toString()
            MyApp.currentCountry = fullPlaceData?.country.toString()

            _searchedFormattedAddress.postValue(fullPlaceData?.address.toString())
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
            _dataState.postValue(DataState.LOADING)
            _nearByPlacesInGroup.postValue(DUMMY_PLACE_TYPE_UI_MODEL)
            isNewDataFetched = true
            val interestList =
                if (MyApp.tempPlaceInterestes.isNullOrEmpty()) placesRepository.getAllSavedPlaceTypePreferences() else emptyList()
            val response = placesRepository.fetchPlacesNearMeAndSaveInLocalDb(
                latitude,
                longitude,
                if (MyApp.tempPlaceDistance != null) MyApp.tempPlaceDistance!! else radius,
                if (MyApp.tempPlaceInterestes != null) MyApp.tempPlaceInterestes!!.map { it.id } else interestList.map { it.id },
                true
            )
            if (response is Resource.Error) {
                _dataState.postValue(DataState.EMPTY_DATA)
                _nearByPlacesInGroup.postValue(emptyList())
                _nearByPlaces.postValue(ArrayList(emptyList()))
                _nearByPlacesMarkerPoints.postValue(emptyList())
                _isLoading.emit(false)
            }else if (response is Resource.Success){
                if(response.data.isNullOrEmpty()){
                    _dataState.postValue(DataState.EMPTY_DATA)
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
            placesRepository.getPlacesNearMeFromLocalDb().map { ArrayList(it) }
                .map { placesList ->
                    placesList.map { place ->
                        place.shouldShowCheckBox = isPlaceGeneratedOptionClicked
                        place
                    }
                }
                .collect { listOfPlaceUiModel ->
                    val latLngs = listOfPlaceUiModel.map { it.latLng }
                    val placesInGroupData = listOfPlaceUiModel.groupBy { it.superType }
                    val places = ArrayList<PlaceTypeUiModel>()
                    placesInGroupData.forEach {
                        val sortedPlaces = sortPlaceBasedOnSortType(it.value, sortType)
                        if (sortedPlaces.isNotEmpty()) {
                            val createdPlaces = sortedPlaces.map { uiModel ->
                                PlaceTypeUiModel(
                                    place = uiModel
                                )
                            }
                            places.add(PlaceTypeUiModel(categoryTitle = it.key))
                            places.addAll(createdPlaces)
                        }
                    }
                    if (places.isEmpty()) {
                        _dataState.postValue(DataState.EMPTY_DATA)
                        _nearByPlacesInGroup.postValue(emptyList())
                        _nearByPlaces.postValue(ArrayList(emptyList()))
                        _nearByPlacesMarkerPoints.postValue(emptyList())
                        _isLoading.emit(false)
                    } else {
                        nearByMarkerList.addAll(latLngs)
                        _dataState.postValue(DataState.DATA)
                        _nearByPlacesInGroup.postValue(places)
                        if (isNewDataFetched) {
                            _nearByPlaces.postValue(listOfPlaceUiModel)
                            _nearByPlacesMarkerPoints.postValue(listOfPlaceUiModel)
                        }
                        _isLoading.emit(false)
                        _selectedPlaces.postValue(listOfPlaceUiModel.filter { it.isChecked })
                    }

            }
        }
    }

    private fun sortPlaceBasedOnSortType(sortList : List<PlaceUiModel>,sortType: SortType): List<PlaceUiModel> {
        if (MyApp.userCurrentLatLng == null) return sortList
       return when (sortType) {
            SortType.DISTANCE -> {
                sortList.map { place ->
                    val distance = calculateDistance(
                        MyApp.userCurrentLatLng?.latitude!!,
                        MyApp.userCurrentLatLng?.longitude!!,
                        place.latLng?.latitude!!,
                        place.latLng.longitude
                    ) / 1000
                    place to distance
                }
                    .sortedBy { it.second }
                    .map { it.first }
            }

            SortType.MOST_POPULAR -> {
                sortList.sortedByDescending { it.reviewsCount }
            }

            SortType.HIGHEST_RATING -> {
                sortList.sortedByDescending { it.rating }
            }

            SortType.A_TO_Z -> {
                sortList.sortedBy { it.name }
            }

            SortType.COST_LOW_TO_HIGH -> {
                sortList.filter { it.pricingType != null }.mapNotNull { place ->
                    val priceType = when (place.pricingType?.toLowerCase()) {
                        "inexpensive" -> 0
                        "moderate" -> 1
                        "expensive" -> 2
                        else -> -1
                    }
                    place to priceType
                }.sortedBy { it.second }.map { it.first }
            }

            SortType.COST_HIGH_TO_LOW -> {
                sortList.filter { it.pricingType != null }.mapNotNull { place ->
                    val priceType = when (place.pricingType?.toLowerCase()) {
                        "inexpensive" -> 0
                        "moderate" -> 1
                        "expensive" -> 2
                        else -> -1
                    }
                    place to priceType
                }.sortedByDescending { it.second }.map { it.first }
            }

            SortType.OPEN_NOW -> {
                sortList.filter { it.placeOpenStatus.equals("open", true) }
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
        isPlaceGeneratedOptionClicked = true
        isNewDataFetched = false
        _showItineraryGenerationLayout.value = true
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(true,true)
        }
    }

    fun onItineraryGenerationCancelledClicked(isForceCancel: Boolean = true) {
        isNewDataFetched = false
        isPlaceGeneratedOptionClicked = false
        _showItineraryGenerationLayout.value = false
        if(isForceCancel){
            getNearByPlaces()
        }else{
            viewModelScope.launch(Dispatchers.IO) {
                placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(false,false)
            }
        }
    }

    fun onPlaceSelectedForItinerary(placeId: String?, checked: Boolean) {
        if (placeId == null) return
        isNewDataFetched = false
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.updatePlaceIsChecked(placeId, checked)
        }
    }

    fun onOpenNowFilterClicked() {
        isNewDataFetched = false
        sortType = SortType.OPEN_NOW
        getNearByPlaces()
    }

    fun sortOptionSelected(sortType: SortType) {
        isNewDataFetched = false
        this.sortType = sortType
        getNearByPlaces()
    }

    enum class PlaceUiState {
        HORIZONTAL, VERTICAL, NONE
    }

    enum class DataState {
        LOADING, DATA,EMPTY_DATA
    }


}