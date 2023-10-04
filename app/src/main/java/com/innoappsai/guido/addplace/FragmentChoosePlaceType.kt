package com.innoappsai.guido.addplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentChoosePlaceTypeBinding
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentChoosePlaceType :
    BaseFragment<FragmentChoosePlaceTypeBinding>(FragmentChoosePlaceTypeBinding::inflate) {

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
            rvPlaceTypes.apply {
                adapter = adapterPlaceTypes
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            tvNext.setOnClickListener {
                viewModel.goToAddPlaceDetails()
            }
        }
        viewModel.apply {
            getAllPlaceTypes()
            placeTypes.observe(viewLifecycleOwner) {
                adapterPlaceTypes.setPlacesType(it)
            }
            currentScreenName.collectIn(viewLifecycleOwner) {
                findNavController().navigate(R.id.fragmentAddPlaceAddressDetails)
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
        }
        adapterPlaceTypes.setOnPlaceTypeSelected {
            viewModel.onPlaceTypeSelected(it)
        }

    }


}