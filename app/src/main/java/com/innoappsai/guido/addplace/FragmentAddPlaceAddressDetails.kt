package com.innoappsai.guido.addplace

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesAutoCompleteAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceAddressDetailsBinding
import com.innoappsai.guido.fragments.SearchLocationViewModel
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceAddressDetails :
    BaseFragment<FragmentAddPlaceAddressDetailsBinding>(FragmentAddPlaceAddressDetailsBinding::inflate) {

    private var isSearchedAddressUsingApi = false
    private val viewModel: AddPlaceViewModel by activityViewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter
    private lateinit var adapterPlaceAutoComplete: PlacesAutoCompleteAdapter

    private val searchLocationViewModel: SearchLocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivArrowBack.setOnClickListener { findNavController().popBackStack() }
            etPlaceStreetAddress.setText(viewModel.getStreetAddress())
            etPlacePlaceCity.setText(viewModel.getCityName())
            etPlacePlaceState.setText(viewModel.getStateName())
            etPlacePlaceCountry.setText(viewModel.getCountryName())
            etPlaceStreetAddress.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrEmpty() && !isSearchedAddressUsingApi) {
                    searchLocationViewModel.getPredictions(text.toString())
                }
                binding.rvPlaceSuggestions.isVisible =
                    !text.isNullOrEmpty() && !isSearchedAddressUsingApi
            }
            rvPlaceSuggestions.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            tvNext.setOnClickListener {
                val placeName = binding.etPlaceName.text.toString()
                val placeStreetAddress = binding.etPlaceStreetAddress.text.toString()
                val placeCityName = binding.etPlacePlaceCity.text.toString()
                val placeStateName = binding.etPlacePlaceState.text.toString()
                val placeCountryName = binding.etPlacePlaceCountry.text.toString()
                val placePinCode = binding.etPlacePlacePincode.text.toString()


                tiLayoutPlaceName.error = null
                tiLayoutPlaceStreetAddress.error = null
                tiLayoutPlaceCity.error = null
                tiLayoutPlaceCountry.error = null
                tiLayoutPlaceState.error = null

                if (placeName.isNullOrEmpty()) {
                    tiLayoutPlaceName.error = "Please Select Your Place Name"
                    return@setOnClickListener
                }

                if (placeStreetAddress.isNullOrEmpty()) {
                    tiLayoutPlaceStreetAddress.error = "Please Select Your Place Street Address"
                    return@setOnClickListener
                }


                if (placeCityName.isNullOrEmpty()) {
                    tiLayoutPlaceCity.error = "Please Select Your Place City"
                    return@setOnClickListener
                }
                if (placeCountryName.isNullOrEmpty()) {
                    tiLayoutPlaceCountry.error = "Please Select Your Place Country"
                    return@setOnClickListener
                }
                if (placeStateName.isNullOrEmpty()) {
                    tiLayoutPlaceState.error = "Please Select Your Place State Name"
                    return@setOnClickListener
                }

                viewModel.setPlaceAddressDetails(
                    placeName,
                    placeStreetAddress,
                    placeCityName,
                    placeStateName,
                    placeCountryName,
                    placePinCode
                )
            }
        }

        viewModel.apply {
            navigateNext.collectIn(viewLifecycleOwner) {
                findNavController().navigate(R.id.fragmentAddPlaceMoreDetails)
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
            searchedFormattedAddress.observe(viewLifecycleOwner) {
                binding.apply {
                    etPlaceStreetAddress.setText(viewModel.getStreetAddress())
                    etPlacePlaceCity.setText(viewModel.getCityName())
                    etPlacePlaceState.setText(viewModel.getStateName())
                    etPlacePlaceCountry.setText(viewModel.getCountryName())
                }
                adapterPlaceAutoComplete.setPredications(emptyList())
                isSearchedAddressUsingApi = false
            }
        }

        searchLocationViewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                binding.rvPlaceSuggestions.isVisible = it.isNotEmpty()
                adapterPlaceAutoComplete.setPredications(it)
            }
        }
        adapterPlaceAutoComplete.setOnPlaceSelected {
            binding.rvPlaceSuggestions.isVisible = false
            isSearchedAddressUsingApi = true
            viewModel.fetchCurrentAddressFromGeoCoding(
                it.latitude,
                it.longitude
            )
        }

    }


}