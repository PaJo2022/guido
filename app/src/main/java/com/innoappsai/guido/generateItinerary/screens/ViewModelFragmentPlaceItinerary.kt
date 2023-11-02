package com.innoappsai.guido.generateItinerary.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
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

    private val _generatedItineraryWithTravelPlaceAndDirection: MutableLiveData<List<List<TravelPlaceWithTravelDirection>>> =
        MutableLiveData()
    val generatedItineraryWithTravelPlaceAndDirection: LiveData<List<List<TravelPlaceWithTravelDirection>>> =
        _generatedItineraryWithTravelPlaceAndDirection

    private val _moveToDayIndex: MutableLiveData<Int> = MutableLiveData()
    val moveToDayIndex: LiveData<Int> = _moveToDayIndex


    private var itineraryModel: ItineraryModel? = null


    fun generatePlaceItineraryById() {
        viewModelScope.launch(Dispatchers.IO) {
            val generatedItinerary = itineraryRepository.getItineraryById("").first()
            Log.i("JAPAN", "generatePlaceItineraryById: ${generatedItinerary}")
            generatedItinerary?.let {
                itineraryModel = generatedItinerary.travelItineraryData

                val tripData = generatedItinerary.travelItineraryData.tripData!!
                val data =
                    ArrayList<ArrayList<TravelPlaceWithTravelDirection>>()
                tripData.forEach {
                    val travelPlaces = it.travelPlaces ?: emptyList()
                    val travelPlaceWithTravelDirectionList =
                        arrayListOf<TravelPlaceWithTravelDirection>()
                    travelPlaces.forEachIndexed { index, item ->
                        var travelDirection: TravelDirection? = null
                        if (index >= 0 && index < travelPlaces.size - 1) {
                            travelDirection = TravelDirection()
                            val nextPlace = travelPlaces[index + 1]
                            travelDirection.currentLocation = LatLng(item.latitude, item.longitude)
                            travelDirection.nextLocation =
                                LatLng(nextPlace.latitude, nextPlace.longitude)
                            travelDirection.currentLocationName = item.placeName
                            travelDirection.nextLocationName = nextPlace.placeName

                        }


                        val travelPlaceWithTravelDirection1 =
                            TravelPlaceWithTravelDirection(travelPlace = item)

                        travelPlaceWithTravelDirectionList.add(travelPlaceWithTravelDirection1)
                        if (travelDirection != null) {
                            val travelPlaceWithTravelDirection2 =
                                TravelPlaceWithTravelDirection(travelDirection = travelDirection)
                            travelPlaceWithTravelDirectionList.add(travelPlaceWithTravelDirection2)
                        }

                    }
                    data.add(travelPlaceWithTravelDirectionList)
                }

                _generatedItineraryWithTravelPlaceAndDirection.postValue(data)

                itineraryModel?.let {
                    _itineraryBasicDetails.postValue(it)
                }
                itineraryModel?.tripData?.firstOrNull()?.isSelected = true
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