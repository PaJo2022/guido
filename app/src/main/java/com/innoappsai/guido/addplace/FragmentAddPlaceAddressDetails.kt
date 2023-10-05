package com.innoappsai.guido.addplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceAddressDetailsBinding
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceAddressDetails :
    BaseFragment<FragmentAddPlaceAddressDetailsBinding>(FragmentAddPlaceAddressDetailsBinding::inflate) {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        }

    }


}