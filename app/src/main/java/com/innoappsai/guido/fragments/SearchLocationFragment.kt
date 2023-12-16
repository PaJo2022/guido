package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.adapters.PlacesAutoCompleteAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentSearchLocationBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {

    private lateinit var adapterPlaceAutoComplete : PlacesAutoCompleteAdapter
    private val viewModel : SearchLocationViewModel by viewModels()
    private val homeViewModel : HomeViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLastSearchedPlaces()
        binding.apply {
            etSearchLocation.doOnTextChanged { text, start, before, count ->
                binding.recentlySearchedTv.isVisible = text.isNullOrEmpty()
                if (!text.isNullOrEmpty()) {
                    viewModel.getPredictions(text.toString())
                }else{
                    viewModel.getLastSearchedPlaces()
                }
            }
            rvLocationSuggestion.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
           ivArrowBack.setOnClickListener {
               parentFragmentManager.popBackStack()
           }
       }

        viewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                adapterPlaceAutoComplete.setPredications(it)
            }
            navigateBack.collectIn(viewLifecycleOwner) {
                homeViewModel.resetData()
                parentFragmentManager.popBackStack()
                homeViewModel.fetchPlacesDetailsNearMe(it.latitude, it.longitude)
                homeViewModel.moveToTheLatLng(LatLng(it.latitude, it.longitude))
            }
        }

        adapterPlaceAutoComplete.setOnPlaceSelected {
            viewModel.getSelectedPlaceLatLon(it)
        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }


}