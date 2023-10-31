package com.innoappsai.guido.generateItinerary.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentPlaceItinerary @Inject constructor(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    private var gson: Gson = Gson()

    private val _itineraryBasicDetails: MutableLiveData<ItineraryModel> = MutableLiveData()
    val itineraryBasicDetails: LiveData<ItineraryModel> = _itineraryBasicDetails

    private val _generatedItinerary: MutableLiveData<List<TripData>> = MutableLiveData()
    val generatedItinerary: LiveData<List<TripData>> = _generatedItinerary

    private val _moveToDayIndex: MutableLiveData<Int> = MutableLiveData()
    val moveToDayIndex: LiveData<Int> = _moveToDayIndex


    private var itineraryModel: ItineraryModel? = null


    fun generatePlaceItineraryById() {
        viewModelScope.launch(Dispatchers.IO) {
            val generatedItinerary = itineraryRepository.getItineraryById("").first()
            Log.i("JAPAN", "generatePlaceItineraryById: ${generatedItinerary}")
            generatedItinerary?.let {
                itineraryModel = generatedItinerary.travelItineraryData
                itineraryModel?.let {
                    _itineraryBasicDetails.postValue(it)
                }
                itineraryModel?.tripData?.let { tripData ->
                    _generatedItinerary.postValue(tripData)
                }
            }
        }
    }

    fun onDaySelected(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var indexToMove = 0
            itineraryModel?.tripData?.forEachIndexed {index,data->
                if(data.day == day){
                    indexToMove = index
                }
                data.isSelected = data.day == day
            }
            itineraryModel?.tripData?.let {
                _generatedItinerary.postValue(it)
            }
            _moveToDayIndex.postValue(indexToMove)
        }
    }


}