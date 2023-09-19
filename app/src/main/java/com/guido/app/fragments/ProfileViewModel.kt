package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.data.places.PlacesRepository
import com.guido.app.model.PlaceType
import com.guido.app.model.PlaceTypeContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val placesRepository: PlacesRepository) :
    ViewModel() {

    var distanceProgress: Int = 5
    private val _formattedAddress: MutableLiveData<String> = MutableLiveData()
    val formattedAddress: LiveData<String> get() = _formattedAddress

    fun fetchCurrentAddressFromGeoCoding(latLng: String, key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = placesRepository.fetchAddressFromLatLng(latLng,key)?.results?.firstOrNull()?.formatted_address.toString()
            _formattedAddress.postValue(address)
        }
    }

    val placeTypes = listOf(
        PlaceType("1", "Airport", "Service"),
        PlaceType("2", "Amusement Park", "Entertainment"),
        PlaceType("3", "Aquarium", "Entertainment"),
        PlaceType("4", "Art Gallery", "Entertainment"),
        PlaceType("5", "Bakery", "Shop"),
        PlaceType("6", "Bank", "Shop"),
        PlaceType("7", "Bar", "Shop"),
        PlaceType("8", "Beauty Salon", "Shop"),
        PlaceType("9", "Book Store", "Shop"),
        PlaceType("10", "Bus Station", "Service"),
        PlaceType("11", "Cafe", "Shop"),
        PlaceType("12", "Campground", "Entertainment"),
        PlaceType("13", "Car Rental", "Service"),
        PlaceType("14", "Car Repair", "Service"),
        PlaceType("15", "Casino", "Entertainment"),
        PlaceType("16", "Church", "Service"),
        PlaceType("17", "City Hall", "Service"),
        PlaceType("18", "Clothing Store", "Shop"),
        PlaceType("19", "Convenience Store", "Shop"),
        PlaceType("20", "Courthouse", "Service"),
        PlaceType("21", "Dentist", "Service"),
        PlaceType("22", "Department Store", "Shop"),
        PlaceType("23", "Doctor", "Service"),
        PlaceType("24", "Electrician", "Service"),
        PlaceType("25", "Electronics Store", "Shop"),
        PlaceType("26", "Embassy", "Service"),
        PlaceType("27", "Fire Station", "Service"),
        PlaceType("28", "Florist", "Shop"),
        PlaceType("29", "Furniture Store", "Shop"),
        PlaceType("30", "Gas Station", "Service"),
        PlaceType("31", "Gym", "Shop"),
        PlaceType("32", "Hair Care", "Shop"),
        PlaceType("33", "Hardware Store", "Shop"),
        PlaceType("34", "Hindu Temple", "Service"),
        PlaceType("35", "Home Goods Store", "Shop"),
        PlaceType("36", "Hospital", "Service"),
        PlaceType("37", "Hotel", "Lodging"),
        PlaceType("38", "Jewelry Store", "Shop"),
        PlaceType("39", "Laundry", "Service"),
        PlaceType("40", "Library", "Entertainment"),
        PlaceType("41", "Liquor Store", "Shop"),
        PlaceType("42", "Lodging", "Lodging"),
        PlaceType("43", "Meal Delivery", "Service"),
        PlaceType("44", "Meal Takeaway", "Service"),
        PlaceType("45", "Mosque", "Service"),
        PlaceType("46", "Movie Theater", "Entertainment"),
        PlaceType("47", "Museum", "Entertainment"),
        PlaceType("48", "Night Club", "Entertainment"),
        PlaceType("49", "Painter", "Service"),
        PlaceType("50", "Park", "Entertainment"),
        PlaceType("51", "Parking", "Service"),
        PlaceType("52", "Pharmacy", "Shop"),
        PlaceType("53", "Physiotherapist", "Service"),
        PlaceType("54", "Plumber", "Service"),
        PlaceType("55", "Police", "Service"),
        PlaceType("56", "Post Office", "Service"),
        PlaceType("57", "Restaurant", "Shop"),
        PlaceType("58", "Roofing Contractor", "Service"),
        PlaceType("59", "RV Park", "Lodging"),
        PlaceType("60", "School", "Service"),
        PlaceType("61", "Shoe Store", "Shop"),
        PlaceType("62", "Shopping Mall", "Shop"),
        PlaceType("63", "Spa", "Shop"),
        PlaceType("64", "Stadium", "Entertainment"),
        PlaceType("65", "Store", "Shop"),
        PlaceType("66", "Subway Station", "Service"),
        PlaceType("67", "Supermarket", "Shop"),
        PlaceType("68", "Synagogue", "Service"),
        PlaceType("69", "Taxi Stand", "Service"),
        PlaceType("70", "Tourist Attraction", "Entertainment"),
        PlaceType("71", "Train Station", "Service"),
        PlaceType("72", "Travel Agency", "Service"),
        PlaceType("73", "University", "Education"),
        PlaceType("74", "Zoo", "Entertainment")
    )
    val userInterestes : MutableLiveData<List<PlaceTypeContainer>> = MutableLiveData()

    init {
        getSavedPlaceTypePreferences()
    }

        private fun getSavedPlaceTypePreferences(){
            viewModelScope.launch {
                placesRepository.getAllSavedPlaceTypePreferences().collect{savedPlaceInterestTypes->
                    val job = async {
                        placeTypes.forEach { placetype->
                            placetype.isSelected = savedPlaceInterestTypes.find { it.id == placetype.id} != null
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
        }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch {
            val isSelected = placeTypes.find { it.id == id }?.isSelected ?: false
            placeTypes.find { it.id == id }?.isSelected = !isSelected
            val groupedPlaceTypes = placeTypes.groupBy { it.type }

            // Map the grouped results into PlaceTypeContainer objects
            val placeTypeContainers = groupedPlaceTypes.map { (type, placeTypeList) ->
                PlaceTypeContainer(type, placeTypeList)
            }
            userInterestes.postValue(placeTypeContainers)
        }
    }

    fun savePlaceTypePreferences(){
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
        }
    }



}