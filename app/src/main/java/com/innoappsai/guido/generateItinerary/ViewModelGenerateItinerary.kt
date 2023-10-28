package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.R
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection
import com.innoappsai.guido.generateItinerary.model.InterestsSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelGenerateItinerary @Inject constructor() : ViewModel() {

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


    init {
        _travelDurationState.value = travelDuration
        _interestsSliderListState.value = interestsSliderList
    }
}