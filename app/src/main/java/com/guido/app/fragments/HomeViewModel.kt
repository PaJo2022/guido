package com.guido.app.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.style.CharacterStyle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.guido.app.Constants
import com.guido.app.LocationClient
import com.guido.app.MyApp
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiType
import com.guido.app.model.placesUiModel.addUiType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
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


    private val _nearByPlacesMarkerPoints: MutableStateFlow<List<LatLng?>> = MutableStateFlow(
        emptyList()
    )
    val nearByPlacesMarkerPoints: MutableStateFlow<List<LatLng?>> get() = _nearByPlacesMarkerPoints

    private val _moveToLocation: MutableLiveData<Pair<LatLng, Boolean>> = MutableLiveData()
    val moveToLocation: LiveData<Pair<LatLng, Boolean>> get() = _moveToLocation

    private val _currentLatLng: MutableLiveData<LatLng> = MutableLiveData()
    val currentLatLng: LiveData<LatLng> get() = _currentLatLng


    private val _placeUiState: MutableLiveData<PlaceUiState> = MutableLiveData()
    val placeUiState: LiveData<PlaceUiState> get() = _placeUiState


    private val markerDataList : ArrayList<LatLng?> = ArrayList<LatLng?>()

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

    private fun fetchSearchedLocationAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
           // _searchedFormattedAddress.postValue(address)
        }
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
            markerDataList.clear()
//            placesRepository.getAllSavedPlaceTypePreferences().collect {
//                if(it.size > 5) return@collect
//                val job = async {
//                    it.forEach { placeType ->
//                        val job2 = async {
//                            placesRepository.fetchPlacesNearMe(
//                                location, radius, type, placeType.id, key
//                            )
//                        }
//                        val attraction = job2.await()
//                        val placeTypeUiModel = PlaceTypeUiModel(
//                            placeType.displayName,
//                            attraction.firstOrNull()?.icon,
//                            attraction
//                        )
//                        nearByPlacesListInGroup.add(placeTypeUiModel)
//                        nearByPlacesList.addAll(attraction)
//                    }
//                }
//                job.await()
//                _nearByPlacesInGroup.emit(ArrayList(nearByPlacesListInGroup))
//                _nearByPlaces.emit(ArrayList(nearByPlacesList))
//            }
            val interestList =
                arrayListOf<PlaceType>(PlaceType("bar", "bar", "bar"), PlaceType("bank", "bank", "bank"))
            val job = async {
                interestList.forEach { placeType ->
                    val job2 = async {
                        placesRepository.fetchPlacesNearMe(
                            location, radius, type, placeType.id, key
                        )
                    }
                    val attraction = job2.await()
                    val latLangs = attraction.map { it.latLng }
                    val placeTypeUiModel = PlaceTypeUiModel(
                        placeType.displayName,
                        attraction.firstOrNull()?.icon,
                        attraction.addUiType(PlaceUiType.LARGE),
                        )
                    nearByPlacesListInGroup.add(placeTypeUiModel)
                    nearByPlacesList.addAll(attraction)
                    ArrayList(latLangs).let { markerDataList.addAll(it) }
                }
            }
            job.await()
            _nearByPlacesInGroup.postValue(ArrayList(nearByPlacesListInGroup))
            _nearByPlaces.postValue(ArrayList(nearByPlacesList))
            _nearByPlacesMarkerPoints.emit(ArrayList(markerDataList))


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

    fun showVerticalUi(){
        _placeUiState.value = PlaceUiState.VERTICAL
    }

    fun showNone(){
        _placeUiState.value = PlaceUiState.NONE
    }

    enum class PlaceUiState{
        HORIZONTAL,VERTICAL,NONE
    }


}