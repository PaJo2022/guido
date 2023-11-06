package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
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
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelPlaceTypes
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelStartDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentGenerateItineraryOptions :
    BaseFragment<FragmentItinearyGenerationStep1Binding>(FragmentItinearyGenerationStep1Binding::inflate) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var itineraryOptionPagerAdapter: ItineraryOptionPagerAdapter

    private var currentStep = 1



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itineraryOptionPagerAdapter = ItineraryOptionPagerAdapter(requireActivity())
        binding.apply {
            rvItineraryGenerationOptions.apply {
                adapter = itineraryOptionPagerAdapter
                isUserInputEnabled = false
            }
            onFirstStep()
            btnNext.setOnClickListener {
                onStepDone()
            }
            btnPrev.setOnClickListener {
                onStepPrevious()
            }
        }

        addOnBackPressedCallback {
            if(currentStep == 0){
                requireActivity().finish()
            }else{
                onStepPrevious()
            }
        }

        itineraryOptionPagerAdapter.setItineraryOptions(
            arrayListOf(
                FragmentTravelDuration(),
                FragmentTravelStartDate(),
                FragmentTravelCompanionType(),
                FragmentTravelPlaceTypes(),
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
        val newPosition = if (currentPosition == itineraryGenerationOptionList.size - 1) {
            findNavController().navigate(R.id.fragmentPlaceDreamCard)
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