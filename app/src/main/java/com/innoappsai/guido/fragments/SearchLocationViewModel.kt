package com.innoappsai.guido.fragments

import android.graphics.Typeface
import android.text.style.CharacterStyle
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

    private val STYLE_BOLD: CharacterStyle
        get() {
            return android.text.style.StyleSpan(Typeface.BOLD)
        }
    private val STYLE_NORMAL: CharacterStyle
        get() {
            return android.text.style.StyleSpan(Typeface.NORMAL)
        }


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
            delay(500)
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


}