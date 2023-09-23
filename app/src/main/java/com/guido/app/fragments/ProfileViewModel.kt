package com.guido.app.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.auth.repo.user.UserRepository
import com.guido.app.data.places.PlacesRepository
import com.guido.app.db.AppPrefs
import com.guido.app.model.PlaceType
import com.guido.app.model.PlaceTypeContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val userRepository: UserRepository,
    private val appPrefs: AppPrefs
) :
    ViewModel() {

    var distanceProgress: Int = 5
    private val _formattedAddress: MutableLiveData<String> = MutableLiveData()
    val formattedAddress: LiveData<String> get() = _formattedAddress

    fun fetchCurrentAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(
                latLng,
                key
            )?.results?.firstOrNull()?.formatted_address.toString()
            _formattedAddress.postValue(address)
        }
    }


    val placeTypes = listOf(
        PlaceType("1", "Airport", "Service"),
        PlaceType("2", "Amusement Park", "Entertainment"),


        PlaceType("6", "Bank", "Shop"),
        PlaceType("7", "Bar", "Shop"),
        PlaceType("8", "Beauty Salon", "Shop"),


        PlaceType("11", "Cafe", "Shop"),
        PlaceType("12", "Campground", "Entertainment"),


        PlaceType("16", "Church", "Service"),
        PlaceType("17", "City Hall", "Service"),
        PlaceType("18", "Clothing Store", "Shop"),


        PlaceType("21", "Dentist", "Service"),
        PlaceType("22", "Department Store", "Shop"),


        PlaceType("26", "Embassy", "Service"),
        PlaceType("27", "Fire Station", "Service"),
        PlaceType("28", "Florist", "Shop"),


        PlaceType("31", "Gym", "Shop"),
        PlaceType("32", "Hair Care", "Shop"),


        PlaceType("36", "Hospital", "Service"),
        PlaceType("37", "Hotel", "Lodging"),
        PlaceType("38", "Jewelry Store", "Shop"),


        PlaceType("41", "Liquor Store", "Shop"),
        PlaceType("42", "Lodging", "Lodging"),


        PlaceType("46", "Movie Theater", "Entertainment"),
        PlaceType("47", "Museum", "Entertainment"),
        PlaceType("48", "Night Club", "Entertainment"),

        PlaceType("51", "Parking", "Service"),
        PlaceType("52", "Pharmacy", "Shop"),


        PlaceType("56", "Post Office", "Service"),
        PlaceType("57", "Restaurant", "Shop"),
        PlaceType("58", "Roofing Contractor", "Service"),

        PlaceType("61", "Shoe Store", "Shop"),
        PlaceType("62", "Shopping Mall", "Shop"),
        PlaceType("63", "Spa", "Shop"),


        PlaceType("66", "Subway Station", "Service"),
        PlaceType("67", "Supermarket", "Shop"),



    )

    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()

    private val _isPlaceInterestesSaved: MutableSharedFlow<Unit> = MutableSharedFlow()
    val isPlaceInterestesSaved: SharedFlow<Unit> = _isPlaceInterestesSaved


    init {
        getSavedPlaceTypePreferences()
    }

        private fun getSavedPlaceTypePreferences(){
            viewModelScope.launch(Dispatchers.IO) {
                val preferences = placesRepository.getAllSavedPlaceTypePreferences()
                val job = async {
                    placeTypes.forEach { placetype->
                        placetype.isSelected = preferences.find { it.id == placetype.id} != null
                    }
                }
                job.await()
                val groupedPlaceTypes = placeTypes.groupBy { it.type }

                // Map the grouped results into PlaceTypeContainer objects
                val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                    PlaceTypeContainer(type, placeTypeList)
                }
                userInterestes.postValue(placeTypeContainers)
            }
        }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch {
            val isSelected = placeTypes.find { it.id == id }?.isSelected ?: false
            placeTypes.find { it.id == id }?.isSelected = !isSelected
            savePlaceTypePreferences()
        }
    }

   fun savePlaceTypePreferences() {
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
            appPrefs.prefDistance = distanceProgress
            _isPlaceInterestesSaved.emit(Unit)
        }
    }


    fun getUserData() = userRepository.getUserDetailsFlow(appPrefs.userId.toString())

    fun getSavedPreferences() = placesRepository.getAllSavedPlaceTypePreferencesFlow()
}