package com.innoappsai.guido

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.innoappsai.guido.model.PlaceFeature
import com.innoappsai.guido.model.PlaceType

object Constants {
    const val GCP_API_KEY = "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY"


    fun isAlarmSet(context: Context, requestCode: Int, intent: Intent): Boolean {
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent != null
    }
    // Finance category
    val financeList = listOf(
        PlaceType("accounting", "Accounting", "Finance"),
        PlaceType("atm", "ATM", "Finance"),
        PlaceType("bank", "Bank", "Finance"),
        PlaceType("insurance_agency", "Insurance Agency", "Finance")
    )

    // Services category
    val servicesList = listOf(
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
        PlaceType("real_estate_agency", "Real Estate Agency", "Services")
    )

    // Emergency category
    val emergencyList = listOf(
        PlaceType("fire_station", "Fire Station", "Emergency"),
        PlaceType("police", "Police", "Emergency")
    )

    // Shopping category
    val shoppingList = listOf(
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


    fun getPlaceTypeIcon(type: String): Int {
        for (placeType in placeTypes) {
            if (placeType.type.equals(type,true)) {
                return placeType.iconDrawable
            }
        }
        return R.drawable.airport
    }



    fun getTypesOfPlaces(id: String): String {
        for (placeType in placeTypes) {
            if (placeType.id.equals(id,true)) {
                return placeType.type
            }
        }
        return "airport"
    }

    val placeTypes2 = listOf(
        PlaceType("restaurant", "Restaurant", "Food & Beverages", iconDrawable = R.drawable.icon_restaurant),
        PlaceType("cafe", "Cafe", "Food & Beverages", iconDrawable = R.drawable.ic_cafe_marker),
        PlaceType("hotel", "Hotel", "Lodging", iconDrawable = R.drawable.icon_hotel),
        PlaceType("tourist_information", "Tourist Information", "Travel", iconDrawable = R.drawable.ic_tourist_attraction),
        PlaceType("atm", "ATM", "Finance", iconDrawable = R.drawable.ic_atm),
        PlaceType("supermarket", "Supermarket", "Shopping", iconDrawable = R.drawable.ic_super_market),
        PlaceType("pharmacy", "Pharmacy", "Medical", iconDrawable = R.drawable.icon_pharmacy),
        PlaceType("bus_station", "Bus Station", "Travel", iconDrawable = R.drawable.ic_bus_station),
        PlaceType("car_rental", "Car Rental", "Services", iconDrawable = R.drawable.ic_car_rental),
        PlaceType("charging_station", "Charging Station", "Services", iconDrawable = R.drawable.car_repair),
        PlaceType("airport", "Airport", "Travel", iconDrawable = R.drawable.ic_airport),
        PlaceType("picnic_site", "Picnic Spot", "Travel", iconDrawable = R.drawable.icon_campground),
        PlaceType("museum", "Museum", "Tourist Spots", iconDrawable = R.drawable.icon_museum),
        PlaceType("hospital", "Hospital", "Medical", iconDrawable = R.drawable.hospital_marker),
        PlaceType("historic_site", "Historic Site", "Travel", iconDrawable = R.drawable.ic_travel_agency),
        PlaceType("gallery", "Art Gallery", "Travel", iconDrawable = R.drawable.ic_art_gallery),
        PlaceType("park", "Park", "Tourist Spots", iconDrawable = R.drawable.ic_park),
        PlaceType("beach", "Beach", "Tourist Spots", iconDrawable = R.drawable.ic_park),
        PlaceType("viewpoint", "View Point", "Tourist Spots", iconDrawable = R.drawable.ic_park),

    )


    val placeTypes = listOf(
        PlaceType("atm", "ATM", "Finance", iconDrawable = R.drawable.ic_atm),
        PlaceType("bank", "Bank", "Finance", iconDrawable = R.drawable.ic_bank),
        PlaceType(
            "insurance_agency",
            "Insurance Agency",
            "Finance",
            iconDrawable = R.drawable.ic_insurance_agency
        ),

        PlaceType("car_dealer", "Car Dealer", "Services", iconDrawable = R.drawable.ic_car_dealer),
        PlaceType("car_rental", "Car Rental", "Services", iconDrawable = R.drawable.ic_car_rental),
        PlaceType("car_repair", "Car Repair", "Services", iconDrawable = R.drawable.ic_car_repair),
        PlaceType("car_wash", "Car Wash", "Services", iconDrawable = R.drawable.ic_car_wash),
        PlaceType("florist", "Florist", "Services", iconDrawable = R.drawable.ic_florist),
        PlaceType("laundry", "Laundry", "Services", iconDrawable = R.drawable.ic_laundry),
        PlaceType("plumber", "Plumber", "Services", iconDrawable = R.drawable.ic_plumber),
        PlaceType("storage", "Storage", "Services", iconDrawable = R.drawable.ic_storage),
        PlaceType("courthouse", "Courthouse", "Services", iconDrawable = R.drawable.ic_courthouse),
        PlaceType("post_office", "Post Office", "Services", iconDrawable = R.drawable.ic_post_office),
        PlaceType("locksmith", "Locksmith", "Services", iconDrawable = R.drawable.ic_locksmith),
        PlaceType("painter", "Painter", "Services", iconDrawable = R.drawable.ic_painter),
        PlaceType("electrician", "Electrician", "Services", iconDrawable = R.drawable.ic_electrician),
        PlaceType("lawyer", "Lawyer", "Services", iconDrawable = R.drawable.ic_lawyer),
        PlaceType("local_government_office", "Local Government Office", "Services", iconDrawable = R.drawable.ic_local_government_office),
        PlaceType("embassy", "Embassy", "Services", iconDrawable = R.drawable.ic_embassy),
        PlaceType("gym", "Gym", "Services", iconDrawable = R.drawable.icon_gym),
        PlaceType("roofing_contractor", "Roofing Contractor", "Services", iconDrawable = R.drawable.icon_roofing_contractor),
        PlaceType("real_estate_agency", "Real Estate Agency", "Services", iconDrawable = R.drawable.ic_real_estate_agency),

        PlaceType("fire_station", "Fire Station", "Emergency", iconDrawable = R.drawable.fire_station_marker),
        PlaceType("police", "Police", "Emergency", iconDrawable = R.drawable.ic_police_station),

        PlaceType("clothing_store", "Clothing Store", "Shopping", iconDrawable = R.drawable.icon_clothing_store),
        PlaceType("convenience_store", "Convenience Store", "Shopping", iconDrawable = R.drawable.ic_convenience_store),
        PlaceType("bicycle_store", "Bicycle Store", "Shopping", iconDrawable = R.drawable.ic_bicycle_store),
        PlaceType("book_store", "Book Store", "Shopping", iconDrawable = R.drawable.ic_book_store),
        PlaceType("store", "Store", "Shopping", iconDrawable = R.drawable.ic_store),
        PlaceType("department_store", "Department Store", "Shopping", iconDrawable = R.drawable.icon_department_store),
        PlaceType("supermarket", "Supermarket", "Shopping", iconDrawable = R.drawable.ic_super_market),
        PlaceType("electronics_store", "Electronics Store", "Shopping", iconDrawable = R.drawable.ic_electronics_store),
        PlaceType("jewelry_store", "Jewelry Store", "Shopping", iconDrawable = R.drawable.ic_jewelry_store),
        PlaceType("furniture_store", "Furniture Store", "Shopping", iconDrawable = R.drawable.ic_furniture_store),
        PlaceType("shoe_store", "Shoe Store", "Shopping", iconDrawable = R.drawable.ic_shoe_store),
        PlaceType("shopping_mall", "Shopping Mall", "Shopping", iconDrawable = R.drawable.ic_shopping_mall),
        PlaceType("hardware_store", "Hardware Store", "Shopping", iconDrawable = R.drawable.ic_hardware_store),
        PlaceType("home_goods_store", "Home Goods Store", "Shopping", iconDrawable = R.drawable.ic_home_goods_store),
        PlaceType("pet_store", "Pet Store", "Shopping", iconDrawable = R.drawable.ic_pet_store),


        PlaceType("airport", "Airport", "Travel", iconDrawable = R.drawable.ic_airport),
        PlaceType("bus_station", "Bus Station", "Travel", iconDrawable = R.drawable.ic_bus_station),
        PlaceType("taxi_stand", "Taxi Stand", "Travel", iconDrawable = R.drawable.ic_taxi_stand),
        PlaceType("travel_agency", "Travel Agency", "Travel", iconDrawable = R.drawable.ic_travel_agency),
        PlaceType("subway_station", "Subway Station", "Travel", iconDrawable = R.drawable.ic_subway),
        PlaceType("train_station", "Train Station", "Travel", iconDrawable = R.drawable.ic_train_station),
        PlaceType("transit_station", "Transit Station", "Travel"),
        PlaceType("light_rail_station", "Light Rail Station", "Travel"),
        PlaceType("gas_station", "Gas Station", "Travel"),
        PlaceType("parking", "Parking", "Travel", iconDrawable = R.drawable.icon_parking),
        PlaceType("lodging", "Lodging", "Travel", iconDrawable = R.drawable.icon_hotel),

        PlaceType("amusement_park", "Amusement Park", "Entertainment", iconDrawable = R.drawable.amusment_park_marker),
        PlaceType("aquarium", "Aquarium", "Entertainment", iconDrawable = R.drawable.ic_aquarium),
        PlaceType("art_gallery", "Art Gallery", "Entertainment", iconDrawable = R.drawable.ic_art_gallery),
        PlaceType("casino", "Casino", "Entertainment", iconDrawable = R.drawable.ic_casino),
        PlaceType("bowling_alley", "Bowling Alley", "Entertainment", iconDrawable = R.drawable.ic_bowling_alley),
        PlaceType("movie_rental", "Movie Rental", "Entertainment", iconDrawable = R.drawable.ic_movie_rental),
        PlaceType("movie_theater", "Movie Theater", "Entertainment", iconDrawable = R.drawable.ic_movie_theater),
        PlaceType("moving_company", "Moving Company", "Entertainment", iconDrawable = R.drawable.ic_moving_company),
        PlaceType("night_club", "Night Club", "Entertainment", iconDrawable = R.drawable.icon_night_club),

        PlaceType("bakery", "Bakery", "Food & Beverages", iconDrawable = R.drawable.ic_bakery),
        PlaceType("bar", "Bar", "Food & Beverages", iconDrawable = R.drawable.icon_bar),
        PlaceType("cafe", "Cafe", "Food & Beverages", iconDrawable = R.drawable.ic_cafe_marker),
        PlaceType("liquor_store", "Liquor Store", "Food & Beverages", iconDrawable = R.drawable.icon_liquor_store),
        PlaceType("meal_delivery", "Meal Delivery", "Food & Beverages"),
        PlaceType("meal_takeaway", "Meal Takeaway", "Food & Beverages"),
        PlaceType("restaurant", "Restaurant", "Food & Beverages", iconDrawable = R.drawable.icon_restaurant),


        PlaceType("beauty_salon", "Beauty Salon", "Style & Grooming", iconDrawable = R.drawable.ic_beauty_saloon),
        PlaceType("hair_care", "Hair Care", "Style & Grooming", iconDrawable = R.drawable.icon_hair_care),
        PlaceType("spa", "Spa", "Style & Grooming", iconDrawable = R.drawable.ic_spa),


        PlaceType("church", "Church", "Religious", iconDrawable = R.drawable.icon_church),
        PlaceType("cemetery", "Cemetery", "Religious", iconDrawable = R.drawable.ic_cemetery),
        PlaceType("funeral_home", "Funeral Home", "Religious", iconDrawable = R.drawable.ic_funeral_home),
        PlaceType("mosque", "Mosque", "Religious", iconDrawable = R.drawable.ic_mosque),
        PlaceType("synagogue", "Synagogue", "Religious", iconDrawable = R.drawable.ic_synagogue),
        PlaceType("hindu_temple", "Hindu Temple", "Religious", iconDrawable = R.drawable.ic_hindu_temple),

        PlaceType("zoo", "Zoo", "Tourist Spots", iconDrawable = R.drawable.ic_zoo),
        PlaceType("campground", "Campground", "Tourist Spots", iconDrawable = R.drawable.icon_campground),
        PlaceType("tourist_attraction", "Tourist Attraction", "Tourist Spots", iconDrawable = R.drawable.ic_tourist_attraction),
        PlaceType("city_hall", "City Hall", "Tourist Spots", iconDrawable = R.drawable.ic_city_hall),
        PlaceType("museum", "Museum", "Tourist Spots", iconDrawable = R.drawable.icon_museum),
        PlaceType("stadium", "Stadium", "Tourist Spots", iconDrawable = R.drawable.ic_stadium),
        PlaceType("park", "Park", "Tourist Spots", iconDrawable = R.drawable.ic_park),
        PlaceType("rv_park", "RV Park", "Tourist Spots", iconDrawable = R.drawable.ic_park),

        PlaceType("pharmacy", "Pharmacy", "Medical", iconDrawable = R.drawable.icon_pharmacy),
        PlaceType("physiotherapist", "Physiotherapist", "Medical", iconDrawable = R.drawable.ic_physiotherapist),
        PlaceType("hospital", "Hospital", "Medical", iconDrawable = R.drawable.hospital_marker),
        PlaceType("dentist", "Dentist", "Medical", iconDrawable = R.drawable.icon_dentist),
        PlaceType("doctor", "Doctor", "Medical", iconDrawable = R.drawable.ic_doctor),
        PlaceType("veterinary_care", "Veterinary Care", "Medical", iconDrawable = R.drawable.ic_veterinary_care),
        PlaceType("drugstore", "Drugstore", "Medical", iconDrawable = R.drawable.ic_drugstore),


        PlaceType("library", "Library", "Education", iconDrawable = R.drawable.ic_library),
        PlaceType("university", "University", "Education", iconDrawable = R.drawable.ic_university),
        PlaceType("school", "School", "Education", iconDrawable = R.drawable.ic_school),
        PlaceType("primary_school", "Primary School", "Education", iconDrawable = R.drawable.ic_school),
        PlaceType("secondary_school", "Secondary School", "Education", iconDrawable = R.drawable.ic_school)

    )


    val placeFeaturesList = listOf(
        PlaceFeature("Outdoor Sitting"),
        PlaceFeature("Reservation"),
        PlaceFeature("Wifi"),
        PlaceFeature("Wheelchair Access"),
        PlaceFeature("Vegetarian Friendly")
    )


    //Folder Paths
    const val USER_FILE_FOLDER = "user"


    val iconResourceMapping = mapOf(
        "R.drawable.ic_account_firm" to R.drawable.ic_account_firm,
        "R.drawable.ic_atm" to R.drawable.ic_atm,
        "R.drawable.ic_bank" to R.drawable.ic_bank,
        "R.drawable.ic_insurance_agency" to R.drawable.ic_insurance_agency,
        "R.drawable.ic_car_dealer" to R.drawable.ic_car_dealer,
        "R.drawable.ic_car_rental" to R.drawable.ic_car_rental,
        "R.drawable.ic_car_repair" to R.drawable.ic_car_repair,
        "R.drawable.ic_car_wash" to R.drawable.ic_car_wash,
        "R.drawable.ic_florist" to R.drawable.ic_florist,
        "R.drawable.ic_laundry" to R.drawable.ic_laundry,
        "R.drawable.ic_plumber" to R.drawable.ic_plumber,
        "R.drawable.ic_storage" to R.drawable.ic_storage,
        "R.drawable.ic_courthouse" to R.drawable.ic_courthouse,
        "R.drawable.ic_post_office" to R.drawable.ic_post_office,
        "R.drawable.ic_locksmith" to R.drawable.ic_locksmith,
        "R.drawable.ic_painter" to R.drawable.ic_painter,
        "R.drawable.ic_electrician" to R.drawable.ic_electrician,
        "R.drawable.ic_lawyer" to R.drawable.ic_lawyer,
        "R.drawable.ic_local_government_office" to R.drawable.ic_local_government_office,
        "R.drawable.ic_embassy" to R.drawable.ic_embassy,
        "R.drawable.icon_gym" to R.drawable.icon_gym,
        "R.drawable.icon_roofing_contractor" to R.drawable.icon_roofing_contractor,
        "R.drawable.ic_real_estate_agency" to R.drawable.ic_real_estate_agency,
        "R.drawable.fire_station_marker" to R.drawable.fire_station_marker,
        "R.drawable.ic_police_station" to R.drawable.ic_police_station,
        "R.drawable.icon_clothing_store" to R.drawable.icon_clothing_store,
        "R.drawable.ic_convenience_store" to R.drawable.ic_convenience_store,
        "R.drawable.ic_bicycle_store" to R.drawable.ic_bicycle_store,
        "R.drawable.ic_book_store" to R.drawable.ic_book_store,
        "R.drawable.ic_store" to R.drawable.ic_store,
        "R.drawable.icon_department_store" to R.drawable.icon_department_store,
        "R.drawable.ic_super_market" to R.drawable.ic_super_market,
        "R.drawable.ic_electronics_store" to R.drawable.ic_electronics_store,
        "R.drawable.ic_jewelry_store" to R.drawable.ic_jewelry_store,
        "R.drawable.ic_furniture_store" to R.drawable.ic_furniture_store,
        "R.drawable.ic_shoe_store" to R.drawable.ic_shoe_store,
        "R.drawable.ic_shopping_mall" to R.drawable.ic_shopping_mall,
        "R.drawable.ic_hardware_store" to R.drawable.ic_hardware_store,
        "R.drawable.ic_home_goods_store" to R.drawable.ic_home_goods_store,
        "R.drawable.ic_pet_store" to R.drawable.ic_pet_store,
        "R.drawable.ic_airport" to R.drawable.ic_airport,
        "R.drawable.ic_bus_station" to R.drawable.ic_bus_station,
        "R.drawable.ic_taxi_stand" to R.drawable.ic_taxi_stand,
        "R.drawable.ic_travel_agency" to R.drawable.ic_travel_agency,
        "R.drawable.ic_subway" to R.drawable.ic_subway,
        "R.drawable.ic_train_station" to R.drawable.ic_train_station,
        "R.drawable.ic_transit_station" to R.drawable.ic_train_station,
        "R.drawable.ic_light_rail_station" to R.drawable.ic_train_station,
        "R.drawable.ic_gas_station" to R.drawable.ic_gas_station,
        "R.drawable.icon_parking" to R.drawable.icon_parking,
        "R.drawable.icon_hotel" to R.drawable.icon_hotel,
        "R.drawable.amusment_park_marker" to R.drawable.amusment_park_marker,
        "R.drawable.ic_aquarium" to R.drawable.ic_aquarium,
        "R.drawable.ic_art_gallery" to R.drawable.ic_art_gallery,
        "R.drawable.ic_casino" to R.drawable.ic_casino,
        "R.drawable.ic_bowling_alley" to R.drawable.ic_bowling_alley,
        "R.drawable.ic_movie_rental" to R.drawable.ic_movie_rental,
        "R.drawable.ic_movie_theater" to R.drawable.ic_movie_theater,
        "R.drawable.ic_moving_company" to R.drawable.ic_moving_company,
        "R.drawable.icon_night_club" to R.drawable.icon_night_club,
        "R.drawable.ic_bakery" to R.drawable.ic_bakery,
        "R.drawable.icon_bar" to R.drawable.icon_bar,
        "R.drawable.ic_cafe_marker" to R.drawable.ic_cafe_marker,
        "R.drawable.icon_liquor_store" to R.drawable.icon_liquor_store,
        "R.drawable.ic_meal_delevery" to R.drawable.ic_meal_delevery,
        "R.drawable.ic_meal_takeway" to R.drawable.ic_meal_takeway,
        "R.drawable.icon_restaurant" to R.drawable.icon_restaurant,
        "R.drawable.ic_beauty_saloon" to R.drawable.ic_beauty_saloon,
        "R.drawable.icon_hair_care" to R.drawable.icon_hair_care,
        "R.drawable.ic_spa" to R.drawable.ic_spa,
        "R.drawable.icon_church" to R.drawable.icon_church,
        "R.drawable.ic_cemetery" to R.drawable.ic_cemetery,
        "R.drawable.ic_funeral_home" to R.drawable.ic_funeral_home,
        "R.drawable.ic_mosque" to R.drawable.ic_mosque,
        "R.drawable.ic_synagogue" to R.drawable.ic_synagogue,
        "R.drawable.ic_hindu_temple" to R.drawable.ic_hindu_temple,
        "R.drawable.ic_zoo" to R.drawable.ic_zoo,
        "R.drawable.icon_campground" to R.drawable.icon_campground,
        "R.drawable.ic_tourist_attraction" to R.drawable.ic_tourist_attraction,
        "R.drawable.ic_city_hall" to R.drawable.ic_city_hall,
        "R.drawable.icon_museum" to R.drawable.icon_museum,
        "R.drawable.ic_stadium" to R.drawable.ic_stadium,
        "R.drawable.ic_park" to R.drawable.ic_park,
        "R.drawable.ic_park" to R.drawable.ic_park,
        "R.drawable.icon_pharmacy" to R.drawable.icon_pharmacy,
        "R.drawable.ic_physiotherapist" to R.drawable.ic_physiotherapist,
        "R.drawable.hospital_marker" to R.drawable.hospital_marker,
        "R.drawable.icon_dentist" to R.drawable.icon_dentist,
        "R.drawable.ic_doctor" to R.drawable.ic_doctor,
        "R.drawable.ic_veterinary_care" to R.drawable.ic_veterinary_care,
        "R.drawable.ic_drugstore" to R.drawable.ic_drugstore,
        "R.drawable.ic_library" to R.drawable.ic_library,
        "R.drawable.ic_university" to R.drawable.ic_university,
        "R.drawable.ic_school" to R.drawable.ic_school,
        "R.drawable.ic_school" to R.drawable.ic_school
    )




    val travelTypes = listOf(
        PlaceType("airport", "Airport", "Travel", iconDrawable = R.drawable.ic_airport),
        PlaceType("bus_station", "Bus Station", "Travel", iconDrawable = R.drawable.ic_bus_station),
        PlaceType("taxi_stand", "Taxi Stand", "Travel", iconDrawable = R.drawable.ic_taxi_stand),
        PlaceType("travel_agency", "Travel Agency", "Travel", iconDrawable = R.drawable.ic_travel_agency),
        PlaceType("subway_station", "Subway Station", "Travel", iconDrawable = R.drawable.ic_subway),
        PlaceType("train_station", "Train Station", "Travel", iconDrawable = R.drawable.ic_train_station),
        PlaceType("transit_station", "Transit Station", "Travel"),
        PlaceType("light_rail_station", "Light Rail Station", "Travel"),
        PlaceType("parking", "Parking", "Travel", iconDrawable = R.drawable.icon_parking),
        PlaceType("lodging", "Lodging", "Travel", iconDrawable = R.drawable.icon_hotel),
        PlaceType("amusement_park", "Amusement Park", "Entertainment", iconDrawable = R.drawable.amusment_park_marker),
        PlaceType("aquarium", "Aquarium", "Entertainment", iconDrawable = R.drawable.ic_aquarium),
        PlaceType("art_gallery", "Art Gallery", "Entertainment", iconDrawable = R.drawable.ic_art_gallery),
        PlaceType("casino", "Casino", "Entertainment", iconDrawable = R.drawable.ic_casino),
        PlaceType("bowling_alley", "Bowling Alley", "Entertainment", iconDrawable = R.drawable.ic_bowling_alley),
        PlaceType("movie_rental", "Movie Rental", "Entertainment", iconDrawable = R.drawable.ic_movie_rental),
        PlaceType("movie_theater", "Movie Theater", "Entertainment", iconDrawable = R.drawable.ic_movie_theater),
        PlaceType("moving_company", "Moving Company", "Entertainment", iconDrawable = R.drawable.ic_moving_company),
        PlaceType("night_club", "Night Club", "Entertainment", iconDrawable = R.drawable.icon_night_club),
        PlaceType("restaurant", "Restaurant", "Food & Beverages", iconDrawable = R.drawable.icon_restaurant),
        PlaceType("cafe", "Cafe", "Food & Beverages", iconDrawable = R.drawable.ic_cafe_marker),
        PlaceType("tourist_attraction", "Tourist Attraction", "Tourist Spots", iconDrawable = R.drawable.ic_tourist_attraction),
        PlaceType("museum", "Museum", "Tourist Spots", iconDrawable = R.drawable.icon_museum),
        PlaceType("stadium", "Stadium", "Tourist Spots", iconDrawable = R.drawable.ic_stadium),
        PlaceType("park", "Park", "Tourist Spots", iconDrawable = R.drawable.ic_park),
        PlaceType("rv_park", "RV Park", "Tourist Spots", iconDrawable = R.drawable.ic_park),
        PlaceType("library", "Library", "Education", iconDrawable = R.drawable.ic_library),
        PlaceType("university", "University", "Education", iconDrawable = R.drawable.ic_university),
        PlaceType("school", "School", "Education", iconDrawable = R.drawable.ic_school),
        PlaceType("primary_school", "Primary School", "Education", iconDrawable = R.drawable.ic_school),
        PlaceType("secondary_school", "Secondary School", "Education", iconDrawable = R.drawable.ic_school)
    )

}
