package com.innoappsai.guido.generateItinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.innoappsai.guido.R
import com.innoappsai.guido.data.tourData.ChatGptRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection
import com.innoappsai.guido.generateItinerary.model.InterestsSelection
import com.innoappsai.guido.generateItinerary.model.Item
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.travelBudgeItemList
import com.innoappsai.guido.generateItinerary.model.travelCompanionItemList
import com.innoappsai.guido.model.chatGptModel.ChatGptRequest
import com.innoappsai.guido.model.chatGptModel.Message
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelGenerateItinerary @Inject constructor(
    private val chatGptRepository: ChatGptRepository,
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


    // Interests Selection Section
    private val interestsSliderList = arrayListOf<InterestsSelection>(
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
        InterestsSelection(
            interestsName = "Historical2",
            interestsIcon = R.drawable.icon_museum,
            interestsLevel = 2f
        ),
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
        viewModelScope.launch(Dispatchers.IO) {
            val dayWiseTimeString = dayWiseTimeSliderList.map {
                "${it.dayName} i will start my tour from ${it.startValue} Hours and will end my day at ${it.endValue}"
            }.joinToString(", ")
            val commaSeparatedStringForSelectedPlaces = selectedPlacesList.map {
                "The Place Name ${it.name} and Place Id is ${it.placeId} and Place Photo links are ${it.photos}"
            }.joinToString(", ")
            val message =
                "I want you to act as a travel agent and generate a compelling travel itinerary for me for my next travel to place name ${selectedPlaceName} and the place address is ${selectedPlaceAddress} these are my details" +
                        "I am starting my Travel on ${startDate}" +
                        "Number of days i wanna stay there is ${travelDuration}" +
                        "The timings of my each day is ${dayWiseTimeString}" +
                        "I am traveling with ${travelCompanionList.find { it.isSelected }?.name}" +
                        "My Traveling budget is ${travelBudgetList.find { it.isSelected }?.name}" +
                        "These are the landmarks i wanna visit ${commaSeparatedStringForSelectedPlaces}" +
                        "Model the data into following json structure" +
                        "{\n" +
                        "  \"Place Name\" : \"Place Name\",\n" +
                        "  \"Address\" :\"Place Address\",\n" +
                        "  \"Trip Length\":\"number of days user wanna travel\",\n" +
                        "  \"Start Date\":\"start date\",\n" +
                        "  \"End Date\":\"end date\",\n" +
                        "  \"tripData\":[\n" +
                        "    {\n" +
                        "      \"Day\" : \"Day 1\",\n" +
                        "      \"Places\":[\n" +
                        "         {\n" +
                        "          \"timeSlots\":\"select the proper time slots provided by the user for the day and for each place provide 2 hours\",\n" +
                        "          \"placeId\":\"add the place id\",\n" +
                        "          \"placeName\":\"Place Name\",\n" +
                        "          \"placePhotos\":[\n" +
                        "             \"add photo urls\",\n" +
                        "            \"add photo urls\"\n" +
                        "          ]\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"

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