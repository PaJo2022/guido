package com.innoappsai.guido.generateItinerary.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
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
                        val currentTravelPlace = item.details?.toPlaceUiModel()
                        var travelDirection: TravelDirection? = null
                        if (index >= 0 && index < travelPlaces.size - 1) {
                            travelDirection = TravelDirection()

                            val nextPlace = travelPlaces[index + 1].details?.toPlaceUiModel()
                            travelDirection.currentLocation = currentTravelPlace?.latLng
                            travelDirection.nextLocation = nextPlace?.latLng
                            travelDirection.currentLocationName = currentTravelPlace?.name
                            travelDirection.nextLocationName = nextPlace?.name


                        }


                        val travelPlaceWithTravelDirection1 = TravelPlaceWithTravelDirection(
                            travelPlace = TravelPlace(
                                travelTiming = item.travelTiming,
                                placeId = item.landMarkId.toString(),
                                placeName = item.landMarkName.toString(),
                                landMarkDescription = item.landMarkDescription.toString(),
                                placeDetails = item.details?.toPlaceUiModel()
                            )
                        )

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