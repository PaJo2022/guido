package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpots
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItinerary : BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate) {


    private lateinit var mAdapter: AdapterTravelSpots
    private lateinit var mLayoutManager: LinearLayoutManager


    private fun setDataListItems() {

    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvTimeline.layoutManager = mLayoutManager
        mAdapter = AdapterTravelSpots(requireContext())
        binding.rvTimeline.adapter = mAdapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataListItems()
        initRecyclerView()
        val json = "{\n" +
                "  \"Place Name\": \"Panihati\",\n" +
                "  \"Address\": \"Madhyamgram, New Barrakpore, Panihati, Barrackpore - II, North 24 Parganas, West Bengal, 700131, India\",\n" +
                "  \"Trip Length\": 2,\n" +
                "  \"Start Date\": \"31/10/2023\",\n" +
                "  \"End Date\": \"02/11/2023\",\n" +
                "  \"tripData\": [\n" +
                "    {\n" +
                "      \"Day\": \"Day 1\",\n" +
                "      \"Places\": [\n" +
                "        {\n" +
                "          \"timeSlots\": \"12:00 - 14:00\",\n" +
                "          \"placeId\": \"14a20fc4-17d8-4796-98c7-3e5b8f8476ab\",\n" +
                "          \"placeName\": \"My Own Bank\",\n" +
                "          \"placePhotos\": [\n" +
                "            \"https://firebasestorage.googleapis.com/v0/b/guido-ble.appspot.com/o/places_images%2F0883c6c5-c6b8-4d2f-96b8-9ef4f1fe4822?alt=media&token=60c5f791-0b1f-4ed9-ac3e-9bc39ab0596f\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"timeSlots\": \"14:00 - 16:00\",\n" +
                "          \"placeId\": \"03221619-d367-4af0-a6dd-160960c0816e\",\n" +
                "          \"placeName\": \"gugugugugugguguguv\",\n" +
                "          \"placePhotos\": [\n" +
                "            \"https://firebasestorage.googleapis.com/v0/b/guido-ble.appspot.com/o/places_images%2Fdd931eaf-1e8c-4a1a-a57f-5ab0c3cc4fef?alt=media&token=ef596432-a0ae-48aa-b51e-9315967abe4d\"\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"Day\": \"Day 2\",\n" +
                "      \"Places\": [\n" +
                "        {\n" +
                "          \"timeSlots\": \"12:00 - 14:00\",\n" +
                "          \"placeId\": \"8c5588e9-0c2a-4e8e-885b-4dfe008d29a1\",\n" +
                "          \"placeName\": \"test\",\n" +
                "          \"placePhotos\": [\n" +
                "            \"https://firebasestorage.googleapis.com/v0/b/guido-ble.appspot.com/o/places_images%2F0ad150e7-d4d6-453c-9aab-12a0a69c89aa?alt=media&token=a75d0bde-2c7d-4cd2-b0fc-84f21b3ca200\"\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"timeSlots\": \"14:00 - 16:00\",\n" +
                "          \"placeId\": \"d9ddde11-e012-455e-8f46-8cd4105c7bd4\",\n" +
                "          \"placeName\": \"vjbjvv\",\n" +
                "          \"placePhotos\": [\"\"]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n"
        val gson = Gson()

// Convert the JSON string to a Kotlin data class

        val travelData: ItineraryModel = gson.fromJson(json, ItineraryModel::class.java)
        binding.apply {
            tvPlaceName.text = travelData.placeName
            tvPlaceAddress.text = travelData.address
            tvTripExtraDetails.text = "3 days, solo,2023"
        }
        mAdapter.setData(travelData.tripData.first().travelPlaces)
    }
}