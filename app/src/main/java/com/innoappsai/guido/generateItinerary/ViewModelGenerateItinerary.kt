package com.innoappsai.guido.generateItinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection
import com.innoappsai.guido.generateItinerary.model.InterestsSelection
import com.innoappsai.guido.generateItinerary.model.Item
import com.innoappsai.guido.generateItinerary.model.travelBudgeItemList
import com.innoappsai.guido.generateItinerary.model.travelCompanionItemList
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelGenerateItinerary @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val appPrefs: AppPrefs,
) : ViewModel() {

    //
    var selectedPlaceName: String = ""
    var selectedPlaceAddress: String = ""

    // Time Duration
    private var travelDuration = 1

    fun onDurationIncrease() {
        if (travelDuration == 10) return
        travelDuration += 1
        _travelDurationState.value = travelDuration
    }

    fun onDurationDecrease() {
        if (travelDuration == 0) return
        travelDuration -= 1
        _travelDurationState.value = travelDuration
    }

    private val _travelDurationState: MutableLiveData<Int> =
        MutableLiveData()
    val travelDurationState: LiveData<Int> get() = _travelDurationState


    // Time Selection Section
    private val dayWiseTimeSliderList = arrayListOf<DayWiseTimeSelection>()

    private val _dayWiseTimeSelectionListState: MutableLiveData<List<DayWiseTimeSelection>> =
        MutableLiveData()
    val dayWiseTimeSelectionListState: LiveData<List<DayWiseTimeSelection>> get() = _dayWiseTimeSelectionListState

    fun getListOnDayWiseTimeSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            dayWiseTimeSliderList.clear()
            for (i in 1..travelDuration) {
                dayWiseTimeSliderList.add(
                    DayWiseTimeSelection(
                        dayName = "Day ${i}",
                        startValue = 12f,
                        endValue = 20f
                    )
                )
            }
            _dayWiseTimeSelectionListState.postValue(dayWiseTimeSliderList)
        }
    }

    fun onTimeSelectionSliderMoved(id: String, startTime: Float, endTime: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            dayWiseTimeSliderList.find { it.id == id }?.apply {
                this.startValue = startTime
                this.endValue = endTime
            }

        }
    }

    // Place Type Selection Section
    private var selectPlaceTypes = listOf<String>()
    private var nearByPlaces: List<PlaceUiModel> = emptyList()

    fun onPlaceTypeSelected(types: List<String>) {
        selectPlaceTypes = types
    }

    private val _selectedTypePlacesNearLocation: MutableLiveData<List<PlaceUiModel>> =
        MutableLiveData()
    val selectedTypePlacesNearLocation: LiveData<List<PlaceUiModel>> get() = _selectedTypePlacesNearLocation

    fun fetchPlacesNearLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            val latLng = MyApp.userCurrentLatLng ?: return@launch
            nearByPlaces = placesRepository.fetchPlacesNearLocation(
                latLng.latitude,
                latLng.longitude,
                25000,
                selectPlaceTypes
            )
            nearByPlaces = nearByPlaces.filter { it.photos?.isNotEmpty() == true }
            _selectedTypePlacesNearLocation.postValue(nearByPlaces)
        }
    }


    // Interests Selection Section
    private val interestsSliderList = arrayListOf(
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        )
    )

    private val _interestsSliderListState: MutableLiveData<List<InterestsSelection>> =
        MutableLiveData()
    val interestsSliderListState: LiveData<List<InterestsSelection>> get() = _interestsSliderListState


    fun onInterestsSliderMoved(id: String, value: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            interestsSliderList.find { it.id == id }?.apply {
                this.interestsLevel = value
            }
        }
    }


    // Travel Companions Selection Section
    private val travelCompanionList = ArrayList(travelCompanionItemList)
    private val _travelCompanionListState: MutableLiveData<ArrayList<Item>> =
        MutableLiveData()
    val travelCompanionListState: LiveData<ArrayList<Item>> get() = _travelCompanionListState

    fun onTravelCompanionSelected(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            travelCompanionItemList.forEach {
                it.isSelected = it.id == id
            }
            _travelCompanionListState.postValue(travelCompanionList)
        }
    }


    // Travel Budget Selection Section
    private val travelBudgetList = ArrayList(travelBudgeItemList)
    private val _travelBudgetListState: MutableLiveData<ArrayList<Item>> =
        MutableLiveData()
    val travelBudgetListState: LiveData<ArrayList<Item>> get() = _travelBudgetListState

    fun onTravelBudgetSelected(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            travelBudgetList.forEach {
                it.isSelected = it.id == id
            }
            _travelBudgetListState.postValue(travelBudgetList)
        }
    }


    // Place Selection
    private val selectedPlacesList: ArrayList<PlaceUiModel> = ArrayList()
    fun cardRightSwiped(placeUiModel: PlaceUiModel) {
        selectedPlacesList.add(placeUiModel)
    }


    // Itinerary Generation

    private val _onItineraryGeneration: MutableSharedFlow<String> =
        MutableSharedFlow()
    val onItineraryGeneration: SharedFlow<String> get() = _onItineraryGeneration
    fun generateAiTextForItinerary() {
        //c634ed54-3255-42b4-be14-40591677a47b
        if (nearByPlaces.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val dayWiseTimeString = dayWiseTimeSliderList.map {
                "${it.dayName} i will start my tour from ${it.startValue} Hours and will end my day at ${it.endValue}"
            }.joinToString(", ")
            val commaSeparatedStringForSelectedPlaces = selectedPlacesList.map {
                "I want to visit ${it.name} and whose place Id is ${it.placeId} and the Photo links are ${it.photos?.firstOrNull()} and latitude is ${it.latLng?.latitude} and longitude is ${it.latLng?.longitude}"
            }.joinToString(", ")
            val message = "I want you to act as a travel agent and generate a compelling travel itinerary for me for my next travel to place name ${selectedPlaceName} and the place address is ${selectedPlaceAddress} these are my details about this trip.\n" +
                        "I am willing to go for ${travelDuration} days tour plan with my ${travelCompanionList.find { it.isSelected }?.name}.\n" +
                        "My budget for this tour is ${travelBudgetList.find { it.isSelected }?.name}.\n" +
                        "The timings of my each day is ${dayWiseTimeString}\n" +
                        "I am traveling with ${travelCompanionList.find { it.isSelected }?.name}\n" +
                        "My Traveling budget is ${travelBudgetList.find { it.isSelected }?.name}\n" +
                        "These are the landmarks i wanna visit ${commaSeparatedStringForSelectedPlaces}" +
                        "Now using this data create me a json structure using the demon json structure" +
                        "{\"Place Name\":\"place name\",\"countryName\":\"place country name from address\",\"Trip Length\":1,\"Trip Partners\":\"traveling with\",\"Start Date\":\"2023-11-01\",\"End Date\":\"2023-11-01\",\"tripData\":[{\"Day\":\"Day 1\",\"Date\":\"Date Of the Day in this format  Saturady,25\",\"Places\":[{\"timeSlots\":\"Time Slot Based On User Times\",\"placeId\":\"Place Id\",\"latitude\":\"Place Latitude\",\"longitude\":\"Place Longitude\",\"placeName\":\"Place Name\",\"placePhotos\":[\"images\"]}]}]}\n"
            _onItineraryGeneration.emit(message)
        }

    }

    // Travel Start Date
    private var startDate: String = ""
    fun travelStartDate(formattedDate: String) {
        startDate = formattedDate
    }

    init {
        _travelDurationState.value = travelDuration
        _travelCompanionListState.value = travelCompanionList
        _travelBudgetListState.value = travelBudgetList
        _interestsSliderListState.value = interestsSliderList
    }
}