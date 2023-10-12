package com.innoappsai.guido.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.tourData.ChatGptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentPlaceGenerateItineraryViewModel @Inject constructor(
    private val chatGptRepository: ChatGptRepository
) : ViewModel() {


    val itineraryPlaceInterestList: ArrayList<String> = ArrayList()
    var selectedAccommodation: String? = null
    var selectedTransportation: String? = null
    var selectedSeason: String? = null
    var selectedBudget: String? = null

    private val _generateItinerary: MutableSharedFlow<String> = MutableSharedFlow()
    val generateItinerary: SharedFlow<String> = _generateItinerary.asSharedFlow()

    fun generate(numberOfDaysUserWantToTravel: String, placeAddress: String?) {
        viewModelScope.launch {
            val commaSeparatedString = itineraryPlaceInterestList.joinToString(", ")
            //I want to geenrate a travel itineary for given details of place with    1.list of interest user want to do 2.user's preffered accomodation 3. user preferred transporation 4.user preffered season 5. user preferred budged
            val message =
                "I want you to act as a travel agent and generate a compelling travel itinerary for me for my next travel to ${placeAddress} and these are my preferreses" +
                        "Number of days i wanna stay there is $numberOfDaysUserWantToTravel\n" +
                        "The Things i want to do there are $commaSeparatedString\n" +
                        "This is my preferrred Transporation there ${selectedTransportation}\n" +
                        "This is my preferrred Accommodation there ${selectedAccommodation}\n" +
                        "This is my preferrred budget spent there ${selectedBudget}\n" +
                        "This is my preferrred season to go there ${selectedSeason}\n"
            _generateItinerary.emit(message)
        }


    }
}