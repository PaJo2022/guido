package com.innoappsai.guido.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.places.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentPlaceGenerateItineraryViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {


    val itineraryPlaceInterestList: ArrayList<String> = ArrayList()
    var selectedAccommodation: String? = null
    var selectedTransportation: String? = null
    var selectedSeason: String? = null
    var selectedBudget: String? = null

    private val _generateItinerary: MutableSharedFlow<String> = MutableSharedFlow()
    val generateItinerary: SharedFlow<String> = _generateItinerary.asSharedFlow()

    fun generate(
        numberOfDaysUserWantToTravel: String,
        extraInformation: String,
        placeAddress: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedLandMarks = placesRepository.getSelectedLandMarks()
            placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(false, false)
            val commaSeparatedStringForSelectedPlaces =
                selectedLandMarks.map { "${it.name},${it.city},${it.state},${it.country}" }
                    .joinToString(", ")
            val commaSeparatedStringForInterests = itineraryPlaceInterestList.joinToString(", ")

            val message =
                "I want you to act as a travel agent and generate a compelling travel itinerary for me for my next travel to ${placeAddress} and these are my preferreses" +
                        "Number of days i wanna stay there is ${numberOfDaysUserWantToTravel}\n" +
                        "These are the landmarks i wanna visit ${commaSeparatedStringForSelectedPlaces}\n" +
                        "The Things i want to do there are ${commaSeparatedStringForInterests}\n" +
                        "This is my preferrred Transporation there ${selectedTransportation}\n" +
                        "This is my preferrred Accommodation there ${selectedAccommodation}\n" +
                        "This is my preferrred budget spent there ${selectedBudget}\n" +
                        "This is my preferrred season to go there ${selectedSeason}\n" +
                        "These are some extra information for my travel ${extraInformation}\n"
            _generateItinerary.emit(message)
        }


    }

    fun onItineraryGenerationCancelledClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.updateAllPlacesIsCheckedAndCheckBoxFor(false, false)
        }
    }
}