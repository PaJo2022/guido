package com.guido.app.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.guido.app.Constants
import com.guido.app.LocationClient
import com.guido.app.MyApp
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.MarkerData
import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiType
import com.guido.app.model.placesUiModel.addUiType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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
    private var locationClient: LocationClient
) :
    ViewModel() {


    var lastSearchLocationLatLng: LatLng? = null

    private val _nearByPlacesInGroup: MutableLiveData<List<PlaceTypeUiModel>> =
        MutableLiveData()
    val nearByPlacesInGroup: LiveData<List<PlaceTypeUiModel>> get() = _nearByPlacesInGroup

    private val _nearByPlaces: MutableLiveData<List<PlaceUiModel>> = MutableLiveData()
    val nearByPlaces: LiveData<List<PlaceUiModel>> get() = _nearByPlaces


    private val _nearByPlacesMarkerPoints: MutableStateFlow<List<PlaceUiModel>> = MutableStateFlow(
        emptyList()
    )
    val nearByPlacesMarkerPoints: StateFlow<List<PlaceUiModel>> get() = _nearByPlacesMarkerPoints


    private val _scrollHorizontalPlaceListToPosition: MutableSharedFlow<Int> = MutableSharedFlow()
    val scrollHorizontalPlaceListToPosition: SharedFlow<Int> get() = _scrollHorizontalPlaceListToPosition

    private val _selectedMarker: MutableSharedFlow<Marker> = MutableSharedFlow()
    val selectedMarker: SharedFlow<Marker> get() = _selectedMarker

    private val _moveToLocation: MutableLiveData<Pair<LatLng, Boolean>> = MutableLiveData()
    val moveToLocation: LiveData<Pair<LatLng, Boolean>> get() = _moveToLocation

    private val _currentLatLng: MutableLiveData<LatLng> = MutableLiveData()
    val currentLatLng: LiveData<LatLng> get() = _currentLatLng


    private val _placeUiState: MutableLiveData<PlaceUiState> = MutableLiveData()
    val placeUiState: LiveData<PlaceUiState> get() = _placeUiState


    private val nearByMarkerList : ArrayList<LatLng?> = ArrayList()
    val markerDataList : ArrayList<MarkerData> = ArrayList()

    val predictaedLocations: MutableLiveData<List<PlaceAutocomplete>> = MutableLiveData()

    private val STYLE_BOLD: CharacterStyle
        get() {
            return android.text.style.StyleSpan(Typeface.BOLD)
        }
    private val STYLE_NORMAL: CharacterStyle
        get() {
            return android.text.style.StyleSpan(Typeface.NORMAL)
        }

    private val nearByPlacesListInGroup: ArrayList<PlaceTypeUiModel> = ArrayList()
    private val nearByPlacesList: ArrayList<PlaceUiModel> = ArrayList()

    fun fetchCurrentAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(
                latLng,
                key
            )?.results?.firstOrNull()?.formatted_address.toString()
            MyApp.userCurrentFormattedAddress = address
            //_searchedFormattedAddress.postValue(address)
        }
    }

    fun moveToTheLatLng(latLng: LatLng){
        _moveToLocation.value = Pair(latLng,true)
    }

    private fun fetchSearchedLocationAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
           // _searchedFormattedAddress.postValue(address)
        }
    }

    fun resetSearchWithNewInterestes(){
        if(lastSearchLocationLatLng == null) return
        fetchPlacesDetailsNearMe(
            "${lastSearchLocationLatLng?.latitude},${lastSearchLocationLatLng?.longitude}",
            appPrefs.prefDistance,
            "tourist_attraction",
            "",
            Constants.GCP_API_KEY
        )
    }

    fun formatInterestString(input: String): String {
        // Convert the input string to lowercase
        val lowercaseInput = input.lowercase()

        // Split the lowercase string into words based on space
        val words = lowercaseInput.split(" ")

        // Join the words with underscores to create the final formatted string
        val formattedString = words.joinToString("_")

        return if(words.size == 1) lowercaseInput else formattedString
    }


    @SuppressLint("MissingPermission")
    fun fetchPlacesDetailsNearMe(
        location: String,
        radius: Int,
        type: String,
        keyword: String,
        key: String,
    ) {
        fetchSearchedLocationAddressFromGeoCoding(location,key)
        viewModelScope.launch(Dispatchers.IO) {
            nearByPlacesListInGroup.clear()
            nearByPlacesList.clear()
            nearByMarkerList.clear()
           val interestList =  placesRepository.getAllSavedPlaceTypePreferences()

            if(interestList.size > 5) return@launch
            val job = async {
                interestList.forEach { placeType ->
                    val job2 = async {
                        placesRepository.fetchPlacesNearMe(
                            location, radius, type, formatInterestString(placeType.displayName), key
                        )
                    }
                    val attraction = job2.await()
                    Log.i("JAPAN", "nearByPlacesList:${formatInterestString(placeType.displayName)} ${attraction}")
                    val latLangs = attraction.map { it.latLng }
                    val placeTypeUiModel = PlaceTypeUiModel(
                        placeType.displayName,
                        attraction.firstOrNull()?.icon,
                        attraction.addUiType(PlaceUiType.LARGE),
                    )
                    nearByPlacesListInGroup.add(placeTypeUiModel)
                    nearByPlacesList.addAll(attraction)
                    ArrayList(latLangs).let { nearByMarkerList.addAll(it) }
                }
            }
            job.await()

            _nearByPlacesInGroup.postValue(ArrayList(nearByPlacesListInGroup))
            _nearByPlaces.postValue(ArrayList(nearByPlacesList))
            _nearByPlacesMarkerPoints.emit(ArrayList(nearByPlacesList))

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
            place.latLng?.let { _moveToLocation.postValue(Pair(it, true)) }
            fetchPlacesDetailsNearMe(
                "${place.latLng?.latitude},${place.latLng?.longitude}",
                appPrefs.prefDistance,
                "tourist_attraction",
                "",
                Constants.GCP_API_KEY
            )
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
                    "${latLng.latitude},${latLng.longitude}",
                    appPrefs.prefDistance,
                    "tourist_attraction",
                    "",
                    Constants.GCP_API_KEY
                )
                fetchCurrentAddressFromGeoCoding(
                    "${latLng.latitude},${latLng.longitude}",
                    Constants.GCP_API_KEY
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

            _scrollHorizontalPlaceListToPosition.emit(selectedPosition)
        }
    }

    fun setThePositionForHorizontalPlaceAdapter(pos: Int) {
        viewModelScope.launch {
            _selectedMarker.emit(markerDataList[pos].marker)
        }
    }

    enum class PlaceUiState {
        HORIZONTAL, VERTICAL, NONE
    }


}