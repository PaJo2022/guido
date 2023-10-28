package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentItinearyGenerationStep1Binding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
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

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: ItineraryOptionPagerAdapter

    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItineraryOptionPagerAdapter(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvItineraryGenerationOptions.apply {
                adapter = this@FragmentGenerateItineraryOptions.adapter
                isUserInputEnabled = false
            }
            onFirstStep()
            btnNext.setOnClickListener {
                onStepDone()
            }
        }

        addOnBackPressedCallback {
            if(currentStep == 0){
                requireActivity().finish()
            }else{
                onStepPrevious()
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

    private fun onFirstStep() {
        val currentPosition = binding.rvItineraryGenerationOptions.currentItem
        currentStep = currentPosition
        val totalItems = itineraryGenerationOptionList.size
        val itemProgress = 100 / totalItems
        binding.apply {
            tvSteps.text = "Steps ${currentPosition+1}/${itineraryGenerationOptionList.size}"
            linearProgressIndicator.setProgress( itemProgress, false)
        }
    }

    private fun onStepDone() {
        val currentPosition = binding.rvItineraryGenerationOptions.currentItem
        if(currentPosition == 0){
            viewModel.getListOnDayWiseTimeSelection()
        }
        val newPosition = if (currentPosition == itineraryGenerationOptionList.size) {
            return
        } else {
            currentPosition + 1
        }

        currentStep = newPosition
        val totalItems = itineraryGenerationOptionList.size
        val itemProgress = 100 / totalItems

        val currentProgress = binding.linearProgressIndicator.progress

        binding.apply {
            rvItineraryGenerationOptions.currentItem = newPosition
            tvSteps.text = "Steps ${newPosition+1}/${itineraryGenerationOptionList.size}"
            linearProgressIndicator.setProgress(currentProgress + itemProgress, true)
        }
    }

    private fun onStepPrevious() {
        val currentPosition = binding.rvItineraryGenerationOptions.currentItem
        val newPosition = if (currentPosition == 0) {
            return
        } else {
            currentPosition - 1
        }
        currentStep = newPosition
        val totalItems = itineraryGenerationOptionList.size
        val itemProgress = 100 / totalItems

        val currentProgress = binding.linearProgressIndicator.progress




        binding.apply {
            rvItineraryGenerationOptions.currentItem = newPosition
            tvSteps.text = "Steps ${newPosition+1}/${itineraryGenerationOptionList.size - 1}"
            linearProgressIndicator.setProgress(currentProgress - itemProgress, true)
        }
    }
}