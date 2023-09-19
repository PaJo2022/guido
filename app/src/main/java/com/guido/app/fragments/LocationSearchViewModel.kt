package com.guido.app.fragments

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.style.CharacterStyle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.guido.app.Constants
import com.guido.app.LocationClient
import com.guido.app.MyApp
import com.guido.app.MyApp.Companion.nearByAttractions
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.MarkerData
import com.guido.app.model.PlaceAutocomplete
import com.guido.app.model.PlaceType
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val placesClient: PlacesClient,
    private val appPrefs: AppPrefs,
    private var locationClient: LocationClient
) :
    ViewModel() {


    private val _nearByPlacesInGroup: MutableStateFlow<List<PlaceTypeUiModel>> =
        MutableStateFlow(emptyList())
    val nearByPlacesInGroup: StateFlow<List<PlaceTypeUiModel>> get() = _nearByPlacesInGroup.asStateFlow()

    private val _nearByPlaces: MutableStateFlow<List<PlaceUiModel>> = MutableStateFlow(emptyList())
    val nearByPlaces: StateFlow<List<PlaceUiModel>> get() = _nearByPlaces.asStateFlow()


    private val _currentFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val currentFormattedAddress: LiveData<String> get() = _currentFormattedAddress


    private val _currentLatLng: MutableSharedFlow<LatLng> = MutableSharedFlow()
    val currentLatLng: SharedFlow<LatLng> get() = _currentLatLng


    private val _searchedFormattedAddress: MutableLiveData<String> = MutableLiveData()
    val searchedFormattedAddress: LiveData<String> get() = _searchedFormattedAddress

    val searchedPlaceLatLng: MutableSharedFlow<LatLng> = MutableSharedFlow()

    val markerDataList = mutableListOf<MarkerData>()

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
            _searchedFormattedAddress.postValue(address)
        }
    }

    private fun fetchSearchedLocationAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
            _searchedFormattedAddress.postValue(address)
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
            nearByAttractions.clear()
            nearByPlacesList.clear()
            placesRepository.getAllSavedPlaceTypePreferences().collect {
                if(it.size > 5) return@collect
                val job = async {
                    it.forEach { placeType ->
                        val job2 = async {
                            placesRepository.fetchPlacesNearMe(
                                location, radius, type, placeType.id, key
                            )
                        }
                        val attraction = job2.await()
                        val placeTypeUiModel = PlaceTypeUiModel(
                            placeType.displayName,
                            attraction.firstOrNull()?.icon,
                            attraction
                        )
                        nearByPlacesListInGroup.add(placeTypeUiModel)
                        nearByPlacesList.addAll(attraction)
                    }
                }
                job.await()
                _nearByPlacesInGroup.emit(nearByPlacesListInGroup)
                _nearByPlaces.emit(ArrayList(nearByPlacesList))
            }

        }
    }


    private fun concatenatePlaceTypeIds(placeTypes: List<PlaceType>): String {
        val ids = placeTypes.map { it.id }
        return ids.joinToString("|")
    }


    fun getPredictions(constraint: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resultJob = async {
                val resultList = ArrayList<PlaceAutocomplete>()

                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                val token = AutocompleteSessionToken.newInstance()

                //https://gist.github.com/graydon/11198540
                // Use the builder to create a FindAutocompletePredictionsRequest.
                val request =
                    FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                        //.setLocationBias(bounds)
                        //.setCountry("BD")
                        //.setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(constraint.toString())
                        .build()
                val autocompletePredictions = placesClient.findAutocompletePredictions(request)

                // This method should have been called off the main UI thread. Block and wait for at most
                // 60s for a result from the API.
                try {
                    Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: TimeoutException) {
                    e.printStackTrace()
                }

                if (autocompletePredictions.isSuccessful) {
                    val findAutocompletePredictionsResponse = autocompletePredictions.result
                    val result =
                        findAutocompletePredictionsResponse.autocompletePredictions.map { prediction ->
                            PlaceAutocomplete(
                                prediction.placeId,
                                prediction.getPrimaryText(STYLE_BOLD).toString(),
                                prediction.getFullText(STYLE_NORMAL).toString()
                            )
                        }

                    result
                } else {
                    emptyList()
                }
            }
            val result = resultJob.await()
            predictaedLocations.postValue(result)
        }
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
            place.latLng?.let { searchedPlaceLatLng.emit(it) }
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


    fun fetchCurrentLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocation = locationClient.getCurrentLocation()
            currentLocation?.let {
                _currentLatLng.emit(it)
            }
        }
    }

}