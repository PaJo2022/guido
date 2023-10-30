package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.generateItinerary.FragmentPlaceItineraryViewModel
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpots
import com.innoappsai.guido.generateItinerary.model.itinerary.ItineraryModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItinerary : BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate) {

    private var saveItineraryId: String = ""
    private val viewModel: FragmentPlaceItineraryViewModel by viewModels()
    private lateinit var mAdapter: AdapterTravelSpots
    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveItineraryId = arguments?.getString("ITINERARY_DB_ID", "") ?: ""
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvTimeline.layoutManager = mLayoutManager
        mAdapter = AdapterTravelSpots(requireContext())
        binding.rvTimeline.adapter = mAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("JAPAN", "onViewCreated: ${saveItineraryId}")
        if(saveItineraryId.isNotBlank()){
            viewModel.apply {
                generatePlaceItineraryById(saveItineraryId)
                generatedItinerary.observe(viewLifecycleOwner) { json ->
                    Log.i("JAPAN", "onViewCreated: ${json}")
                    try {
                        val gson = Gson()
                        val travelData: ItineraryModel = gson.fromJson(json, ItineraryModel::class.java)

                        binding.apply {
                            tvPlaceName.text = travelData.placeName
                            tvPlaceName.isSelected = true
                            tvPlaceAddress.text = travelData.address
                            tvPlaceAddress.isSelected = true
                            tvTripExtraDetails.text =
                                "${travelData.tripLength}, ${travelData.tripPartners}"
                        }
                        travelData.tripData?.firstOrNull()?.travelPlaces?.let { mAdapter.setData(it) }
                    }catch (e : Exception){
                        Log.i("JAPAN", "Error: ${e }")
                    }
                }
            }
        }
        initRecyclerView()

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

// Convert the JSON string to a Kotlin data class


    }
}