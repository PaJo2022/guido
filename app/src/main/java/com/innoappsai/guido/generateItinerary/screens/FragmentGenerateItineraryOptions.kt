package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.FragmentItinearyGenerationStep1Binding
import com.innoappsai.guido.generateItinerary.adapters.ItineraryOptionPagerAdapter
import com.innoappsai.guido.generateItinerary.model.itineraryGenerationOptionList
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelBudgetType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelCompanionType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelDuration
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelEachDayTiming
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelInterests
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelStartDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentGenerateItineraryOptions :
    BaseFragment<FragmentItinearyGenerationStep1Binding>(FragmentItinearyGenerationStep1Binding::inflate) {


    private lateinit var adapter: ItineraryOptionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItineraryOptionPagerAdapter(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val snapHelper1: SnapHelper = PagerSnapHelper()
        binding.apply {
            rvItineraryGenerationOptions.apply {
                adapter = this@FragmentGenerateItineraryOptions.adapter
                isUserInputEnabled = false
            }
            btnNext.setOnClickListener {
                val currentPosition = binding.rvItineraryGenerationOptions.currentItem
                val newPosition = if (currentPosition == itineraryGenerationOptionList.size - 1) {
                    0
                } else {
                    currentPosition + 1
                }

                binding.rvItineraryGenerationOptions.currentItem = newPosition

            }
        }

        adapter.setItineraryOptions(
            arrayListOf(
                FragmentTravelDuration(),
                FragmentTravelEachDayTiming(),
                FragmentTravelStartDate(),
                FragmentTravelCompanionType(),
                FragmentTravelInterests(),
                FragmentTravelBudgetType(),
            )
        )
    }
}