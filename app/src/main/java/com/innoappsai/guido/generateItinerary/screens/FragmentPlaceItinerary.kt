package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.fragments.LocationDetailsFragment
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelDate
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpotViewPager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItinerary : BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate) {


    private val viewModel: ViewModelFragmentPlaceItinerary by viewModels()
    private lateinit var mAdapter: AdapterTravelDate
    private lateinit var adapterTravelSpotViewPager: AdapterTravelSpotViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //saveItineraryId = arguments?.getString("ITINERARY_DB_ID", "") ?: ""
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
                isUserInputEnabled = false
                adapter = adapterTravelSpotViewPager
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.apply {
            generatePlaceItineraryById()
            itineraryBasicDetails.observe(viewLifecycleOwner) { travelData ->
                binding.apply {
                    tvPlaceName.text = travelData.placeName
                    tvPlaceName.isSelected = true
                    tvPlaceCountry.text = travelData.countryName
                    tvTripExtraDetails.text =
                        "${travelData.tripLength}, ${travelData.tripPartners}"
                }
            }

            generatedItinerary.observe(viewLifecycleOwner) { tripData ->
                try {

                    mAdapter.setData(tripData)
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            generatedItineraryWithTravelPlaceAndDirection.observe(viewLifecycleOwner){
                try {
                    adapterTravelSpotViewPager.setData(it)
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            moveToDayIndex.observe(viewLifecycleOwner){
                binding.rvTravelTimeline.setCurrentItem(it,true)
            }
        }
        initRecyclerView()

        adapterTravelSpotViewPager.setOnTravelDateClickListener{
            navigateToPlaceDetailsScreen(it)
        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

        mAdapter.setOnTravelDateClickListener{
            viewModel.onDaySelected(it)
        }

    }

    private fun navigateToPlaceDetailsScreen(placeId: String) {
        Bundle().apply {
            putString("PLACE_ID", placeId)
            openNavFragment(
                LocationDetailsFragment(),this
            )
        }
    }

    private fun openNavFragment(
        f: Fragment,
        args: Bundle? = null
    ) {
        FragmentUtils.replaceFragment((activity as MainActivity), binding.mainLayout.id, f,args,true)
    }
}