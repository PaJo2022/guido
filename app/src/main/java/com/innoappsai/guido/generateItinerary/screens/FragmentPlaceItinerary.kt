package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelDate
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpotViewPager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItinerary : BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate) {

    private var saveItineraryId: String = ""
    private val viewModel: ViewModelFragmentPlaceItinerary by viewModels()
    private lateinit var mAdapter: AdapterTravelDate
    private lateinit var adapterTravelSpotViewPager: AdapterTravelSpotViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveItineraryId = "77f9543d-4cca-4b86-9b97-cb0cfbc1197a"
        mAdapter = AdapterTravelDate(requireContext())
        adapterTravelSpotViewPager = AdapterTravelSpotViewPager(requireContext())
    }

    private fun initRecyclerView() {
        val snapHelper = PagerSnapHelper()
        binding.apply {
            rvTravelDates.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                adapter = mAdapter
            }
            rvTravelTimeline.apply {
                snapHelper.attachToRecyclerView(this)
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                adapter = adapterTravelSpotViewPager
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.apply {
            if (saveItineraryId.isNotEmpty()) {
                generatePlaceItineraryById(saveItineraryId)
            }
            itineraryBasicDetails.observe(viewLifecycleOwner) { travelData ->
                binding.apply {
                    tvPlaceName.text = travelData.placeName
                    tvPlaceName.isSelected = true
                    tvPlaceAddress.text = travelData.address
                    tvPlaceAddress.isSelected = true
                    tvTripExtraDetails.text =
                        "${travelData.tripLength}, ${travelData.tripPartners}"
                }
                try {
                    travelData.tripData?.let { adapterTravelSpotViewPager.setData(it) }
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            generatedItinerary.observe(viewLifecycleOwner) { tripData ->
                try {
                    mAdapter.setData(tripData)
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            moveToDayIndex.observe(viewLifecycleOwner){
                binding.rvTravelTimeline.smoothScrollToPosition(it)
            }
        }
        initRecyclerView()

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

        mAdapter.setOnTravelDateClickListener{
            viewModel.onDaySelected(it)
        }

    }
}