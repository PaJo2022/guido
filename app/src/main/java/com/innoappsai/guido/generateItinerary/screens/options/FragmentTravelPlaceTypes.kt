package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.Constants
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelPlaceTypesBinding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.AdapterInterestsSelection
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelPlaceTypes : BaseFragment<LayoutItinearyGenerationTravelPlaceTypesBinding>(
    LayoutItinearyGenerationTravelPlaceTypesBinding::inflate
) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: AdapterInterestsSelection
    val selectedChips = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AdapterInterestsSelection(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cgTravelTypes.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size <= 5) {
                val selectedChipListType =
                    checkedIds.map { group.findViewById<Chip>(it).tag.toString() }
                viewModel.onPlaceTypeSelected(selectedChipListType)
            } else {
                checkedIds.firstOrNull()
                    ?.let { binding.cgTravelTypes.findViewById<Chip>(it).isChecked = false }
                binding.cgTravelTypes.checkedChipIds.removeAt(0)
                requireActivity().showToast("Max 5 types can be selected")
            }
        }
        for (placeType in Constants.travelTypes) {
            val chip = Chip(requireContext())
            chip.text = placeType.displayName
            chip.isCheckable = true
            chip.isChecked = false // You can set this based on your requirements
            chip.tag = placeType.id // Store the ID in the chip's tag for easy reference

            // You can set a Chip icon if it's available
            placeType.iconDrawable.let {
                val icon = ContextCompat.getDrawable(requireContext(), it)
                chip.chipIcon = icon
            }

            binding.cgTravelTypes.addView(chip)
        }


        viewModel.apply {
            interestsSliderListState.observe(viewLifecycleOwner) {
                adapter.setInterestsOptions(it)
            }
        }

        adapter.setOnSliderChangeListener { id, value ->
            viewModel.onInterestsSliderMoved(id, value)
        }
    }
}