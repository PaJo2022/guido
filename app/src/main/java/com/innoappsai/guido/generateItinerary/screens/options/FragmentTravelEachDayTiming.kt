package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelTimeDistributionBinding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.AdapterTimeSelection
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelEachDayTiming : BaseFragment<LayoutItinearyGenerationTravelTimeDistributionBinding>(LayoutItinearyGenerationTravelTimeDistributionBinding::inflate) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: AdapterTimeSelection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AdapterTimeSelection()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvDayTimeSelection.apply {
                adapter = this@FragmentTravelEachDayTiming.adapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

        viewModel.apply {
            dayWiseTimeSelectionListState.observe(viewLifecycleOwner) {
                adapter.setItineraryOptions(it)
            }
        }

        adapter.setOnSliderChangeListener { id, startTime, endTime ->
              viewModel.onTimeSelectionSliderMoved(id, startTime, endTime)
        }
    }
}