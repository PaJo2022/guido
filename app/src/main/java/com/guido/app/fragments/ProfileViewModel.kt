package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.data.places.PlacesRepository
import com.guido.app.model.PlaceType
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


    private val placeTypes = listOf(
        PlaceType("airport", "Airport"),
        PlaceType("amusement_park", "Amusement Park"),
        PlaceType("aquarium", "Aquarium"),
        PlaceType("art_gallery", "Art Gallery"),
        PlaceType("bakery", "Bakery"),
        PlaceType("bank", "Bank"),
        PlaceType("bar", "Bar"),
        PlaceType("beauty_salon", "Beauty Salon"),
        PlaceType("bicycle_store", "Bicycle Store"),
        PlaceType("book_store", "Book Store"),
        PlaceType("bus_station", "Bus Station"),
        PlaceType("cafe", "Cafe"),
        PlaceType("campground", "Campground"),
        PlaceType("car_rental", "Car Rental"),
        PlaceType("car_repair", "Car Repair"),
        PlaceType("casino", "Casino"),
        PlaceType("church", "Church"),
        PlaceType("city_hall", "City Hall"),
        PlaceType("clothing_store", "Clothing Store"),
        PlaceType("convenience_store", "Convenience Store"),
        PlaceType("courthouse", "Courthouse"),
        PlaceType("dentist", "Dentist"),
        PlaceType("department_store", "Department Store"),
        PlaceType("doctor", "Doctor"),
        PlaceType("electrician", "Electrician"),
        PlaceType("electronics_store", "Electronics Store"),
        PlaceType("embassy", "Embassy"),
        PlaceType("fire_station", "Fire Station"),
        PlaceType("florist", "Florist"),
        PlaceType("furniture_store", "Furniture Store"),
        PlaceType("gas_station", "Gas Station"),
        PlaceType("gym", "Gym"),
        PlaceType("hair_care", "Hair Care"),
        PlaceType("hardware_store", "Hardware Store"),
        PlaceType("hindu_temple", "Hindu Temple"),
        PlaceType("home_goods_store", "Home Goods Store"),
        PlaceType("hospital", "Hospital"),
        PlaceType("hotel", "Hotel"),  // Added 'Hotel'
        PlaceType("jewelry_store", "Jewelry Store"),
        PlaceType("laundry", "Laundry"),
        PlaceType("library", "Library"),
        PlaceType("liquor_store", "Liquor Store"),
        PlaceType("lodging", "Lodging"),
        PlaceType("meal_delivery", "Meal Delivery"),
        PlaceType("meal_takeaway", "Meal Takeaway"),
        PlaceType("mosque", "Mosque"),
        PlaceType("movie_theater", "Movie Theater"),
        PlaceType("museum", "Museum"),
        PlaceType("night_club", "Night Club"),
        PlaceType("painter", "Painter"),
        PlaceType("park", "Park"),
        PlaceType("parking", "Parking"),
        PlaceType("pharmacy", "Pharmacy"),
        PlaceType("physiotherapist", "Physiotherapist"),
        PlaceType("plumber", "Plumber"),
        PlaceType("police", "Police"),
        PlaceType("post_office", "Post Office"),
        PlaceType("restaurant", "Restaurant"),
        PlaceType("roofing_contractor", "Roofing Contractor"),
        PlaceType("rv_park", "RV Park"),
        PlaceType("school", "School"),
        PlaceType("shoe_store", "Shoe Store"),
        PlaceType("shopping_mall", "Shopping Mall"),
        PlaceType("spa", "Spa"),
        PlaceType("stadium", "Stadium"),
        PlaceType("store", "Store"),
        PlaceType("subway_station", "Subway Station"),
        PlaceType("supermarket", "Supermarket"),
        PlaceType("synagogue", "Synagogue"),
        PlaceType("taxi_stand", "Taxi Stand"),
        PlaceType("tourist_attraction", "Tourist Attraction"),
        PlaceType("train_station", "Train Station"),
        PlaceType("travel_agency", "Travel Agency"),
        PlaceType("university", "University"),
        PlaceType("zoo", "Zoo")
    )

    val userInterestes : MutableLiveData<List<PlaceType>> = MutableLiveData()

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
                    userInterestes.postValue(placeTypes)
                }
            }
        }

    fun onPlaceInterestClicked(id : String){
        viewModelScope.launch {
            val isSelected = placeTypes.find { it.id == id }?.isSelected ?: false
            placeTypes.find { it.id == id }?.isSelected = !isSelected
            userInterestes.postValue(placeTypes)
        }
    }

    fun savePlaceTypePreferences(){
        viewModelScope.launch {
            val allSelectedPlaceInterestes = placeTypes.filter { it.isSelected }
            placesRepository.saveFavouritePlacePreferences(allSelectedPlaceInterestes)
        }
    }



}