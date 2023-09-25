package com.guido.app

import com.guido.app.model.PlaceType

object Constants {
    const val GCP_API_KEY = "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY"


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














    val placeTypes = listOf(
        PlaceType("accounting", "Accounting", "Finance", iconDrawable = R.drawable.ic_account_firm),
        PlaceType("atm", "ATM", "Finance", iconDrawable = R.drawable.ic_atm),
        PlaceType("bank", "Bank", "Finance", iconDrawable = R.drawable.ic_bank),
        PlaceType("insurance_agency", "Insurance Agency", "Finance", iconDrawable = R.drawable.ic_insurance_agency),

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


}
