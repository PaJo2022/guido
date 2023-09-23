package com.guido.app.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guido.app.R
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
        PlaceType("1", "Airport", "Service", iconDrawable = R.drawable.ic_airport),
        PlaceType("6", "Bank", "Shop", iconDrawable = R.drawable.ic_bank),
        PlaceType("7", "Bar", "Shop", iconDrawable = R.drawable.icon_bar),
        PlaceType("8", "Beauty Salon", "Shop", iconDrawable = R.drawable.ic_beauty_saloon),
        PlaceType("11", "Cafe", "Shop", iconDrawable = R.drawable.ic_cafe_marker),
        PlaceType("18", "Clothing Store", "Shop", iconDrawable = R.drawable.icon_clothing_store),
        PlaceType("28", "Florist", "Shop", iconDrawable = R.drawable.icon_florist),
        PlaceType("31", "Gym", "Shop", iconDrawable = R.drawable.icon_gym),
        PlaceType("32", "Hair Care", "Shop", iconDrawable = R.drawable.icon_hair_care),
        PlaceType("38", "Jewelry Store", "Shop", iconDrawable = R.drawable.icon_jewelry),
        PlaceType("41", "Liquor Store", "Shop", iconDrawable = R.drawable.icon_liquor_store),
        PlaceType(
            "22",
            "Department Store",
            "Shop",
            iconDrawable = R.drawable.icon_department_store
        ),
        PlaceType("52", "Pharmacy", "Shop", iconDrawable = R.drawable.icon_pharmacy),
        PlaceType("57", "Restaurant", "Shop", iconDrawable = R.drawable.icon_restaurant),
        PlaceType("63", "Spa", "Shop", iconDrawable = R.drawable.ic_spa),
        PlaceType("67", "Supermarket", "Shop", iconDrawable = R.drawable.ic_super_market),
        PlaceType("61", "Shoe Store", "Shop", iconDrawable = R.drawable.ic_shoe_store),
        PlaceType("62", "Shopping Mall", "Shop", iconDrawable = R.drawable.ic_shopping_mall),


        PlaceType("16", "Church", "Service", iconDrawable = R.drawable.icon_church),
        PlaceType("17", "City Hall", "Service", iconDrawable = R.drawable.icon_city_hall),
        PlaceType("21", "Dentist", "Service", iconDrawable = R.drawable.icon_dentist),
        PlaceType("26", "Embassy", "Service", iconDrawable = R.drawable.icon_embassy),
        PlaceType("27", "Fire Station", "Service", iconDrawable = R.drawable.icon_fire_station),
        PlaceType("36", "Hospital", "Service", iconDrawable = R.drawable.icon_hospital),
        PlaceType("51", "Parking", "Service", iconDrawable = R.drawable.icon_parking),
        PlaceType("56", "Post Office", "Service", iconDrawable = R.drawable.icon_post_ofc),
        PlaceType(
            "58",
            "Roofing Contractor",
            "Service",
            iconDrawable = R.drawable.icon_roofing_contractor
        ),
        PlaceType("66", "Subway Station", "Service", iconDrawable = R.drawable.ic_subway),

        PlaceType("37", "Hotel", "Lodging", iconDrawable = R.drawable.icon_hotel),
        PlaceType("42", "Lodging", "Lodging", iconDrawable = R.drawable.icon_hotel),

        PlaceType(
            "46",
            "Movie Theater",
            "Entertainment",
            iconDrawable = R.drawable.icon_movie_theater
        ),
        PlaceType("47", "Museum", "Entertainment", iconDrawable = R.drawable.icon_museum),
        PlaceType("48", "Night Club", "Entertainment", iconDrawable = R.drawable.icon_night_club),
        PlaceType("12", "Campground", "Entertainment", iconDrawable = R.drawable.icon_campground),
        PlaceType(
            "2",
            "Amusement Park",
            "Entertainment",
            iconDrawable = R.drawable.ic_amusment_park
        )
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