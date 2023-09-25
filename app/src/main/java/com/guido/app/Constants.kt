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

}
