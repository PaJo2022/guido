package com.guido.app.fragments

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


    private val placeTypes = listOf(
        PlaceType("accounting", "Accounting", "Finance"),
        PlaceType("atm", "ATM", "Finance"),
        PlaceType("bank", "Bank", "Finance"),
        PlaceType("insurance_agency", "Insurance Agency", "Finance"),

        PlaceType("car_dealer", "Car Dealer", "Services"),
        PlaceType("car_rental", "Car Rental", "Services"),
        PlaceType("car_repair", "Car Repair", "Services"),
        PlaceType("car_wash", "Car Wash", "Services"),
        PlaceType("florist", "Florist", "Services"),
        PlaceType("laundry", "Laundry", "Services"),
        PlaceType("plumber", "Plumber", "Services"),
        PlaceType("storage", "Storage", "Services"),
        PlaceType("courthouse", "Courthouse", "Services"),
        PlaceType("post_office", "Post Office", "Services"),
        PlaceType("locksmith", "Locksmith", "Services"),
        PlaceType("painter", "Painter", "Services"),
        PlaceType("electrician", "Electrician", "Services"),
        PlaceType("lawyer", "Lawyer", "Services"),
        PlaceType("local_government_office", "Local Government Office", "Services"),
        PlaceType("embassy", "Embassy", "Services"),
        PlaceType("gym", "Gym", "Services"),
        PlaceType("roofing_contractor", "Roofing Contractor", "Services"),
        PlaceType("real_estate_agency", "Real Estate Agency", "Services"),

        PlaceType("fire_station", "Fire Station", "Emergency"),
        PlaceType("police", "Police", "Emergency"),

        PlaceType("clothing_store", "Clothing Store", "Shopping"),
        PlaceType("convenience_store", "Convenience Store", "Shopping"),
        PlaceType("bicycle_store", "Bicycle Store", "Shopping"),
        PlaceType("book_store", "Book Store", "Shopping"),
        PlaceType("store", "Store", "Shopping"),
        PlaceType("department_store", "Department Store", "Shopping"),
        PlaceType("supermarket", "Supermarket", "Shopping"),
        PlaceType("electronics_store", "Electronics Store", "Shopping"),
        PlaceType("jewelry_store", "Jewelry Store", "Shopping"),
        PlaceType("furniture_store", "Furniture Store", "Shopping"),
        PlaceType("shoe_store", "Shoe Store", "Shopping"),
        PlaceType("shopping_mall", "Shopping Mall", "Shopping"),
        PlaceType("hardware_store", "Hardware Store", "Shopping"),
        PlaceType("home_goods_store", "Home Goods Store", "Shopping"),
        PlaceType("pet_store", "Pet Store", "Shopping")
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