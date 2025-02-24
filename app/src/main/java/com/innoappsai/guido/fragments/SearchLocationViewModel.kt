package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.model.PlaceAutocomplete
import com.innoappsai.guido.model.place_autocomplete.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) :
    ViewModel() {

    var isPredictionSelected = false

    private val _suggestedLocations: MutableLiveData<List<PlaceAutocomplete>> = MutableLiveData()
    val suggestedLocations: LiveData<List<PlaceAutocomplete>> = _suggestedLocations


    private val _navigateBack: MutableSharedFlow<PlaceAutocomplete> = MutableSharedFlow()
    val navigateBack: SharedFlow<PlaceAutocomplete> = _navigateBack

    fun onPredictionSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            isPredictionSelected = true
            delay(1.seconds)
            isPredictionSelected = false
        }
    }

    fun saveSearchPlaceLocationToDb(placeAutocomplete: PlaceAutocomplete) {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.insertNewSearchedLocation(placeAutocomplete)
        }
    }

    fun getLastSearchedPlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.getSearchedLocations().collect {
                _suggestedLocations.postValue(it)
            }
        }
    }

    fun getPredictions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val places = placesRepository.fetchPlaceAutoCompleteSuggestion(query)
            _suggestedLocations.postValue(places.toUiModel())
        }
    }


//    fun getPredictions(constraint: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val resultJob = async {
//                val resultList = ArrayList<PlaceAutocomplete>()
//
//                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//                // and once again when the user makes a selection (for example when calling fetchPlace()).
//                val token = AutocompleteSessionToken.newInstance()
//
//                //https://gist.github.com/graydon/11198540
//                // Use the builder to create a FindAutocompletePredictionsRequest.
//                val request =
//                    FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
//                        //.setLocationBias(bounds)
//                        //.setCountry("BD")
//                        //.setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(constraint.toString())
//                        .build()
//                val autocompletePredictions = placesClient.findAutocompletePredictions(request)
//
//                // This method should have been called off the main UI thread. Block and wait for at most
//                // 60s for a result from the API.
//                try {
//                    Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS)
//                } catch (e: ExecutionException) {
//                    e.printStackTrace()
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                } catch (e: TimeoutException) {
//                    e.printStackTrace()
//                }
//
//                if (autocompletePredictions.isSuccessful) {
//                    val findAutocompletePredictionsResponse = autocompletePredictions.result
//                    val result =
//                        findAutocompletePredictionsResponse.autocompletePredictions.map { prediction ->
//                            PlaceAutocomplete(
//                                prediction.placeId,
//                                prediction.getPrimaryText(STYLE_BOLD).toString(),
//                                prediction.getFullText(STYLE_NORMAL).toString()
//                            )
//                        }
//
//                    result
//                } else {
//                    emptyList()
//                }
//            }
//            val result = resultJob.await()
//            _suggestedLocations.postValue(result)
//        }
//    }

    fun removePredictions() {
        _suggestedLocations.value = emptyList()
    }

    fun getSelectedPlaceLatLon(placeAutocomplete: PlaceAutocomplete) {
        viewModelScope.launch {
            val placeId = placeAutocomplete.placeId
            val placeLocation = placesRepository.getSearchedPlaceLatLng(placeId) ?: return@launch
            placeAutocomplete.latitude = placeLocation.latitude ?: 0.0
            placeAutocomplete.longitude = placeLocation.longitude ?: 0.0
            saveSearchPlaceLocationToDb(placeAutocomplete)
            _navigateBack.emit(placeAutocomplete)
        }
    }


}