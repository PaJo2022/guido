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
        PlaceType("car_rental", "Car Rental", "Services", iconDrawable = R.drawable.car_rental),
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


        PlaceType("airport", "Airport", "Travel"),
        PlaceType("bus_station", "Bus Station", "Travel"),
        PlaceType("taxi_stand", "Taxi Stand", "Travel"),
        PlaceType("travel_agency", "Travel Agency", "Travel"),
        PlaceType("subway_station", "Subway Station", "Travel"),
        PlaceType("train_station", "Train Station", "Travel"),
        PlaceType("transit_station", "Transit Station", "Travel"),
        PlaceType("light_rail_station", "Light Rail Station", "Travel"),
        PlaceType("gas_station", "Gas Station", "Travel"),
        PlaceType("parking", "Parking", "Travel"),
        PlaceType("lodging", "Lodging", "Travel"),

        PlaceType("amusement_park", "Amusement Park", "Entertainment"),
        PlaceType("aquarium", "Aquarium", "Entertainment"),
        PlaceType("art_gallery", "Art Gallery", "Entertainment"),
        PlaceType("casino", "Casino", "Entertainment"),
        PlaceType("bowling_alley", "Bowling Alley", "Entertainment"),
        PlaceType("movie_rental", "Movie Rental", "Entertainment"),
        PlaceType("movie_theater", "Movie Theater", "Entertainment"),
        PlaceType("moving_company", "Moving Company", "Entertainment"),
        PlaceType("night_club", "Night Club", "Entertainment"),

        PlaceType("bakery", "Bakery", "Food & Beverages"),
        PlaceType("bar", "Bar", "Food & Beverages"),
        PlaceType("cafe", "Cafe", "Food & Beverages"),
        PlaceType("liquor_store", "Liquor Store", "Food & Beverages"),
        PlaceType("meal_delivery", "Meal Delivery", "Food & Beverages"),
        PlaceType("meal_takeaway", "Meal Takeaway", "Food & Beverages"),
        PlaceType("restaurant", "Restaurant", "Food & Beverages"),


        PlaceType("beauty_salon", "Beauty Salon", "Style & Grooming"),
        PlaceType("hair_care", "Hair Care", "Style & Grooming"),
        PlaceType("spa", "Spa", "Style & Grooming"),


        PlaceType("church", "Church", "Religious"),
        PlaceType("cemetery", "Cemetery", "Religious"),
        PlaceType("funeral_home", "Funeral Home", "Religious"),
        PlaceType("mosque", "Mosque", "Religious"),
        PlaceType("synagogue", "Synagogue", "Religious"),
        PlaceType("hindu_temple", "Hindu Temple", "Religious"),

        PlaceType("zoo", "Zoo", "Tourist Spots"),
        PlaceType("campground", "Campground", "Tourist Spots"),
        PlaceType("tourist_attraction", "Tourist Attraction", "Tourist Spots"),
        PlaceType("city_hall", "City Hall", "Tourist Spots"),
        PlaceType("museum", "Museum", "Tourist Spots"),
        PlaceType("stadium", "Stadium", "Tourist Spots"),
        PlaceType("park", "Park", "Tourist Spots"),
        PlaceType("rv_park", "RV Park", "Tourist Spots"),

        PlaceType("pharmacy", "Pharmacy", "Medical"),
        PlaceType("physiotherapist", "Physiotherapist", "Medical"),
        PlaceType("hospital", "Hospital", "Medical"),
        PlaceType("dentist", "Dentist", "Medical"),
        PlaceType("doctor", "Doctor", "Medical"),
        PlaceType("veterinary_care", "Veterinary Care", "Medical"),
        PlaceType("drugstore", "Drugstore", "Medical"),


        PlaceType("library", "Library", "Education"),
        PlaceType("university", "University", "Education"),
        PlaceType("school", "School", "Education"),
        PlaceType("primary_school", "Primary School", "Education"),
        PlaceType("secondary_school", "Secondary School", "Education")

    )


}
