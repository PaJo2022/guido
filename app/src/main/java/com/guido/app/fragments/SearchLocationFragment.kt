package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.guido.app.BaseFragment
import com.guido.app.adapters.PlacesAutoCompleteAdapter
import com.guido.app.databinding.FragmentSearchLocationBinding
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
           icArrowBack.setOnClickListener {
               parentFragmentManager.popBackStack()
           }
       }

        viewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner){
                adapterPlaceAutoComplete.setPredications(it)
            }
        }

        adapterPlaceAutoComplete.setOnPlaceSelected {
            viewModel.saveSearchPlaceLocationToDb(it)
            homeViewModel.resetData()
            parentFragmentManager.popBackStack()
            homeViewModel.fetchPlaceDetailsById(it.placeId)

        }
    }


}