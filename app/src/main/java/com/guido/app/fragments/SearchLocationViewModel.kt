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
class SearchLocationViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val placesClient: PlacesClient,
    private val appPrefs: AppPrefs,
    private var locationClient: LocationClient
) :
    ViewModel() {



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
            _suggestedLocations.postValue(result)
        }
    }




}