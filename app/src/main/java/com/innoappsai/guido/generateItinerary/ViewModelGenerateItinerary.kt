package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.R
import com.innoappsai.guido.convertToAMPM
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection
import com.innoappsai.guido.generateItinerary.model.InterestsSelection
import com.innoappsai.guido.generateItinerary.model.Item
import com.innoappsai.guido.generateItinerary.model.Trip
import com.innoappsai.guido.generateItinerary.model.TripDayTiming
import com.innoappsai.guido.generateItinerary.model.generateItinerary.PlaceToVisit
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

    fun getSelectedPlaceTypes() = selectPlaceTypes




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

    private val _onNearByPlaceSelected: MutableLiveData<ArrayList<PlaceUiModel>> =
        MutableLiveData()
    val onNearByPlaceSelected: LiveData<ArrayList<PlaceUiModel>> get() = _onNearByPlaceSelected
    fun cardRightSwiped(placeUiModel: PlaceUiModel) {
        selectedPlacesList.add(placeUiModel)
        _onNearByPlaceSelected.value = selectedPlacesList
    }


    // Itinerary Generation

    private val _onItineraryGeneration: MutableSharedFlow<String> =
        MutableSharedFlow()
    val onItineraryGeneration: SharedFlow<String> get() = _onItineraryGeneration
    fun generateAiTextForItinerary() {
        viewModelScope.launch(Dispatchers.IO) {
            val placeToVisitList = selectedPlacesList.map {
                PlaceToVisit(
                    placeId = it.placeId,
                    latitude = it.latLng?.latitude,
                    longitude = it.latLng?.longitude,
                    placeName = it.name,
                    placePhotos = listOf("${it.photos?.firstOrNull()}")
                )
            }
            val tripEachDayTimings = dayWiseTimeSliderList.mapIndexed { index, data ->
                TripDayTiming(
                    "Day ${index + 1}",
                    convertToAMPM(data.startValue.toInt()),
                    convertToAMPM(data.endValue.toInt())
                )
            }

            val trip = Trip(
                placeName = selectedPlaceName,
                placeCountry = selectedPlaceAddress,
                tripLength = "${travelDuration} Days and ${travelDuration - 1} Night",
                tripPartners = travelCompanionList.find { it.isSelected }?.name.toString(),
                tripStartDate = startDate,
                tripBudget = travelBudgetList.find { it.isSelected }?.name.toString(),
                tripEachDayTimings = tripEachDayTimings,
                tripPlacesWannaVisit = placeToVisitList
            )


            _onItineraryGeneration.emit(createTripSummary(trip))
        }

    }


    fun createTripSummary(trip: Trip): String {
        val sb = StringBuilder()

        // Title
        sb.append("I am going for a Trip to ${trip.placeName}, and using the bellow details create me a travel itinerary\n")
        sb.append("-----------------\n")

        // Location, Duration, Partners, Dates, Budget
        sb.append("Location: ${trip.placeCountry}\n")
        sb.append("Duration: ${trip.tripLength}\n")
        sb.append("Traveling with: ${trip.tripPartners}\n")
        sb.append("Trip Start Date: ${trip.tripStartDate}\n")
        sb.append("Budget: ${trip.tripBudget}\n")

        // Daily Schedule
        sb.append("\nDaily Schedule:\n")
        for ((index, dayTiming) in trip.tripEachDayTimings.withIndex()) {
            sb.append("- ${dayTiming.day}: start day on ${dayTiming.tripStartTime} - and end day on ${dayTiming.tripEndTime}\n")
        }

        // Landmarks to Visit
        sb.append("\nLandmarks to Visit:\n")
        for ((index, landmark) in trip.tripPlacesWannaVisit.withIndex()) {
            sb.append("${index + 1}. ${landmark.placeName}\n")
            sb.append("LandMark Id: ${landmark.placeId}\n")
            sb.append("LandMark: [Latitude: ${landmark.latitude}, Longitude: ${landmark.longitude}]\n")
            sb.append("LandMark Image: ${landmark.placePhotos?.firstOrNull() ?: ""}\n")
        }

        return sb.toString()
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