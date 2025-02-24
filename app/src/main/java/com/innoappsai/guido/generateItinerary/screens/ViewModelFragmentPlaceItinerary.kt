package com.innoappsai.guido.generateItinerary.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.data.travel_itinerary.ItineraryRepository
import com.innoappsai.guido.generateItinerary.model.ItineraryDataForModel
import com.innoappsai.guido.generateItinerary.model.TripDataForNotification
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.Landmark
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.model.places_backend_dto.toPlaceUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ViewModelFragmentPlaceItinerary @Inject constructor(
    private val itineraryRepository: ItineraryRepository
) : ViewModel() {

    private var itineraryId: String? = null
    private var allTimeListWithPlaceIdAndName: List<TripDataForNotification> = emptyList()
    val markerDataList: ArrayList<MarkerData> = ArrayList()

    private val _itineraryBasicDetails: MutableLiveData<ItineraryModel> = MutableLiveData()
    val itineraryBasicDetails: LiveData<ItineraryModel> = _itineraryBasicDetails

    private val _generatedItinerary: MutableLiveData<List<TripData>> = MutableLiveData()
    val generatedItinerary: LiveData<List<TripData>> = _generatedItinerary

    private val _generatedItineraryWithTravelPlaceAndDirection: MutableLiveData<List<List<TravelPlaceWithTravelDirection>>> =
        MutableLiveData()
    val generatedItineraryWithTravelPlaceAndDirection: LiveData<List<List<TravelPlaceWithTravelDirection>>> =
        _generatedItineraryWithTravelPlaceAndDirection

    private val _tripPlaceNotificationList: MutableLiveData<List<TripDataForNotification>> =
        MutableLiveData()
    val tripPlaceNotificationList: LiveData<List<TripDataForNotification>> =
        _tripPlaceNotificationList

    enum class TripItineraryAlarmStatus {
        ACTIVE, NOT_ACTIVE
    }

    private val _tripItineraryAlarmStatus: MutableSharedFlow<TripItineraryAlarmStatus> =
        MutableSharedFlow()
    val tripItineraryAlarmStatus: SharedFlow<TripItineraryAlarmStatus> =
        _tripItineraryAlarmStatus.asSharedFlow()

    private val _tripLocation: MutableLiveData<List<PlaceUiModel?>> =
        MutableLiveData()
    val tripLocation: LiveData<List<PlaceUiModel?>> =
        _tripLocation

    private val _moveMapTo: MutableLiveData<LatLng?> = MutableLiveData()
    val moveMapTo: LiveData<LatLng?> = _moveMapTo

    private val _moveToDayIndex: MutableLiveData<Int> = MutableLiveData()
    val moveToDayIndex: LiveData<Int> = _moveToDayIndex


    private var itineraryModel: ItineraryModel? = null
    var isItineraryIsExpired = false


    fun generatePlaceItineraryById(itineraryId : String) {
        this.itineraryId = itineraryId
        checkItineraryAlarmStatus(itineraryId = itineraryId)
        viewModelScope.launch(Dispatchers.IO) {
            val generatedItinerary = itineraryRepository.getItineraryById(itineraryId)
            val listOfLandMarks = mutableListOf<Landmark>()
            generatedItinerary?.let {
                itineraryModel = generatedItinerary.itineraryModel
                isItineraryIsExpired = generatedItinerary.isExpired == true
                itineraryModel?.tripLength = "${itineraryModel?.tripData?.size}"
                val tripData = generatedItinerary.itineraryModel?.tripData!!
                val data =
                    ArrayList<ArrayList<TravelPlaceWithTravelDirection>>()
                tripData.forEach {
                    val travelPlaces = it.travelPlaces ?: emptyList()
                    val travelPlaceWithTravelDirectionList =
                        arrayListOf<TravelPlaceWithTravelDirection>()
                    travelPlaces.forEachIndexed { index, item ->
                        listOfLandMarks.add(item)
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
                                travelDateAndTiming = item.travelDateAndTiming,
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
                createAlarmTimeListsForThePlaces(listOfLandMarks)
                _generatedItineraryWithTravelPlaceAndDirection.postValue(data)

                itineraryModel?.let {
                    _itineraryBasicDetails.postValue(it)
                }
                itineraryModel?.tripData?.firstOrNull()?.apply {
                    isSelected = true
                    val tripPlaceList = travelPlaces?.map { landMarks ->
                        landMarks.details?.toPlaceUiModel()
                    } ?: emptyList()
                    _tripLocation.postValue(tripPlaceList)
                    val firstPlace = tripPlaceList.firstOrNull()
                    _moveMapTo.postValue(firstPlace?.latLng)
                }
                itineraryModel?.tripData?.let { tripData ->
                    _generatedItinerary.postValue(tripData)
                }
            }
        }
    }


    private fun createAlarmTimeListsForThePlaces(travelPlaces: List<Landmark>) {
        allTimeListWithPlaceIdAndName = travelPlaces.map {
            TripDataForNotification(
                it.landMarkId.toString(),
                it.landMarkName.toString(),
                it.details?.photos?.firstOrNull(),
                it.travelDateAndTiming.toString()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateNotificationTime(taskTime: String, minutesAgo: Int): String? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val taskDateTime = LocalDateTime.parse(taskTime, formatter)
            val notificationDateTime = taskDateTime.minusMinutes(minutesAgo.toLong())

            // Format the notification time back to the desired string format
            formatter.format(notificationDateTime)
        } catch (e: Exception) {
            null
        }
    }


    fun onDaySelected(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var indexToMove = 0
            itineraryModel?.tripData?.forEachIndexed { index, data ->
                if (data.day == day) {
                    indexToMove = index
                }
                data.isSelected = data.day == day
            }
            itineraryModel?.tripData?.let {
                _generatedItinerary.postValue(it)
            }
            itineraryModel?.tripData?.getOrNull(indexToMove)?.let {
                val tripPlaceList = it.travelPlaces?.map { landMarks ->
                    landMarks.details?.toPlaceUiModel()
                } ?: emptyList()
                _tripLocation.postValue(tripPlaceList)
                val firstPlace = tripPlaceList.firstOrNull()
                _moveMapTo.postValue(firstPlace?.latLng)
            }
            _moveToDayIndex.postValue(indexToMove)
        }
    }


    private fun checkItineraryAlarmStatus(itineraryId: String) {
        viewModelScope.launch {
            itineraryRepository.isAlarmSetForTheItineraryIdUsingFlow(itineraryId).collect { it ->
                if (it != null) {
                    _tripItineraryAlarmStatus.emit(TripItineraryAlarmStatus.ACTIVE)
                } else {
                    _tripItineraryAlarmStatus.emit(TripItineraryAlarmStatus.NOT_ACTIVE)
                }
            }

        }
    }

    private fun setAlarmForItineraryId(itineraryId: String) {
        viewModelScope.launch {
            itineraryRepository.addItineraryIdWhenAlarmIsSet(
                ItineraryDataForModel(
                    itineraryId = itineraryId
                )
            )
            _tripPlaceNotificationList.postValue(allTimeListWithPlaceIdAndName)
            _tripItineraryAlarmStatus.emit(TripItineraryAlarmStatus.ACTIVE)
        }
    }

    private fun cancelAlarmForItineraryId(itineraryId: String) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryAlarmForTheItineraryId(itineraryId)
            _tripItineraryAlarmStatus.emit(TripItineraryAlarmStatus.NOT_ACTIVE)
        }
    }

    fun toggleNotificationAlarmForItinerary() {
        if (itineraryId == null) return
        viewModelScope.launch {
            val isAlarmEnabled =
                itineraryRepository.isAlarmSetForTheItineraryId(itineraryId!!) != null
            if (isAlarmEnabled) {
                cancelAlarmForItineraryId(itineraryId!!)
                _tripItineraryAlarmStatus.emit(TripItineraryAlarmStatus.ACTIVE)
            } else {
                setAlarmForItineraryId(itineraryId!!)
            }
        }
    }


}