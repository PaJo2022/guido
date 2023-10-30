package com.innoappsai.guido.generateItinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FragmentPlaceItineraryViewModel @Inject constructor(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {


    private val _generatedItinerary: MutableLiveData<String> = MutableLiveData()
    val generatedItinerary: LiveData<String> = _generatedItinerary


    fun generatePlaceItineraryById(itineraryId: String) {
        itineraryRepository.getItineraryById(itineraryId).onEach {
            _generatedItinerary.postValue(it?.valueInText)
        }.launchIn(viewModelScope)
    }


}