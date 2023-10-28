package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.CurrencyConstant
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentPlaceItinearyGenerationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceGenerateItinerary :
    BaseFragment<FragmentPlaceItinearyGenerationBinding>(FragmentPlaceItinearyGenerationBinding::inflate) {


    private val viewModel: FragmentPlaceGenerateItineraryViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val placeAddress = arguments?.getString("PLACE_ADDRESS")
        val placeCountry = arguments?.getString("PLACE_COUNTRY")

        binding.ivArrowBack.setOnClickListener {
            viewModel.onItineraryGenerationCancelledClicked()
            parentFragmentManager.popBackStack()
        }

        addOnBackPressedCallback {
            viewModel.onItineraryGenerationCancelledClicked()
            parentFragmentManager.popBackStack()
        }

        binding.apply {
            chipGroupInterests.setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedChips = group.checkedChipIds
                viewModel.itineraryPlaceInterestList.clear()
                for (chipId in selectedChips) {
                    val chip = requireActivity().findViewById<Chip>(chipId)
                    val selectedInterest = chip.text.toString()
                    viewModel.itineraryPlaceInterestList.add(selectedInterest)
                }
            }
            accommodationChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedChip = group.findViewById<Chip>(accommodationChipGroup.checkedChipId)
                if (selectedChip != null) {
                    val selectedAccommodation = selectedChip.text.toString()
                    // Use the selectedWeatherSeason in your code
                    viewModel.selectedAccommodation = selectedAccommodation
                }
            }
            transportationChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedChip = group.findViewById<Chip>(transportationChipGroup.checkedChipId)
                if (selectedChip != null) {
                    val selectedTransportation = selectedChip.text.toString()
                    // Use the selectedWeatherSeason in your code
                    viewModel.selectedTransportation = selectedTransportation
                }
            }
            weatherSeasonChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                val selectedChip = group.findViewById<Chip>(weatherSeasonChipGroup.checkedChipId)
                if (selectedChip != null) {
                    val selectedWeatherSeason = selectedChip.text.toString()
                    // Use the selectedWeatherSeason in your code
                    viewModel.selectedSeason = selectedWeatherSeason
                }
            }
            tvMinBudget.text = "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}15,00"
            tvMaxBudget.text =
                "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}1,00,000"
            tvBudgetValue.text =
                "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}15000"
            viewModel.selectedBudget =
                "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}15000"
            seekbarBudget.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvBudgetValue.text =
                        "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}${p1}"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    viewModel.selectedBudget =
                        "${CurrencyConstant.countryCurrencyMap[placeCountry.toString()]}${p0?.progress}"

                }

            })
        }

        binding.btnGenerate.setOnClickListener {
            val numberOfDaysUserWantToTravel = binding.etPlaceStayDays.text.toString()
            val extraInformation = binding.etPlaceExtraInfo.text.toString()
            viewModel.generate(numberOfDaysUserWantToTravel,extraInformation,placeAddress)
        }

        viewModel.apply {
            generateItinerary.collectIn(viewLifecycleOwner) {
                Bundle().apply {
                    putString("GENERATE_ITINEARY_MESSAGE", it)
                    openNavFragment(
                        FragmentPlaceItinerary(),
                        childFragmentManager,
                        "FragmentPlaceItineary",
                        binding.bottomSheet,
                        this
                    )
                }
            }
        }


    }


    private fun openNavFragment(
        f: Fragment?,
        fm: FragmentManager,
        FragmentName: String,
        view: View,
        args: Bundle? = null
    ) {
        val ft = fm.beginTransaction()

        // Pass the bundle as arguments to the fragment, if provided
        if (args != null) {
            f?.arguments = args
        }
        ft.setCustomAnimations(
            R.anim.in_from_right,
            R.anim.out_to_left,
            R.anim.in_from_left,
            R.anim.out_to_right
        )
        ft.replace(view.id, f!!, FragmentName).addToBackStack(FragmentName).commit()
    }

}