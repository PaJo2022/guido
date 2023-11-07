package com.innoappsai.guido.fragments


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelMyTravelItineraryList @Inject constructor(
    private val itineraryRepository: ItineraryRepository,
    private val appPrefs: AppPrefs
) : ViewModel() {

    private val _itineraryList: MutableLiveData<List<ItineraryModel?>> =
        MutableLiveData()
    val itineraryList: LiveData<List<ItineraryModel?>> get() = _itineraryList

    fun getListOfGeneratedItineraryList() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = appPrefs.userId ?: return@launch
            val itineraryList = itineraryRepository.getAllTravelItineraryList(userId)
            itineraryList.map { it.itineraryModel }.let {
                _itineraryList.postValue(it)
            }
        }
    }


}