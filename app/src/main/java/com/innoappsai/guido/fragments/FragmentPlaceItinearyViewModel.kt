package com.innoappsai.guido.fragments

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
class FragmentPlaceItinearyViewModel @Inject constructor(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    var itineraryId: String? = null
    var itineraryText : String ?= null

    private val _generatedItineary: MutableLiveData<String> = MutableLiveData()
    val generatedItineary: LiveData<String> = _generatedItineary

    init {
        itineraryId = UUID.randomUUID().toString()
    }

    fun generatePlaceItineraryById(itineraryId: String) {

        itineraryRepository.getItineraryById(itineraryId).onEach {
            itineraryText = it?.valueInText
            _generatedItineary.postValue(it?.valueInText)
        }.launchIn(viewModelScope)
    }


}