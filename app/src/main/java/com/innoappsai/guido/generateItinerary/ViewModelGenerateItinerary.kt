package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.R
import com.innoappsai.guido.data.places.PlacesRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection
import com.innoappsai.guido.generateItinerary.model.InterestsSelection
import com.innoappsai.guido.generateItinerary.model.Item
import com.innoappsai.guido.generateItinerary.model.generateItinerary.Landmark
import com.innoappsai.guido.generateItinerary.model.generateItinerary.TravelItinerary
import com.innoappsai.guido.generateItinerary.model.generateItinerary.generateTravelItineraryString
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
    var selectedPlaceCountryName: String = ""
    var selectedLatitude: Double ?= null
    var selectedLongitude: Double ?= null
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
                Landmark(
                    id = it.placeId,
                    name = it.name,
                    landMarkType = it.typeName
                )
            }
            val tripMessage = generateTravelItineraryString(
                TravelItinerary(
                    placeName = selectedPlaceName,
                    placeLatitude = selectedLatitude,
                    placeLongitude = selectedLongitude,
                    travelingAs = travelCompanionList.find { it.isSelected }?.name,
                    countryName = selectedPlaceCountryName,
                    tripLength = travelDuration,
                    tripStartDate = startDate,
                    tripBudget = travelBudgetList.find { it.isSelected }?.name,
                    dailySchedule = dayWiseTimeSliderList,
                    landmarks = placeToVisitList
                )
            )

            _onItineraryGeneration.emit(tripMessage)
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