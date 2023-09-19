package com.guido.app.fragments

import android.os.Bundle
import android.view.View
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
    private val sharedViewModel : SharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.apply {
           etSearchLocation.doOnTextChanged { text, start, before, count ->
               if(!text.isNullOrEmpty()){
                   viewModel.getPredictions(text.toString())
               }
           }
           rvLocationSuggestion.apply {
               adapter = adapterPlaceAutoComplete
               layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
           }
           icArrowBack.setOnClickListener {
               findNavController().popBackStack()
           }
       }

        viewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner){
                adapterPlaceAutoComplete.setPredications(it)
            }
        }

        adapterPlaceAutoComplete.setOnPlaceSelected {
            findNavController().popBackStack()
            sharedViewModel.onLocationSelected(it)

        }
    }


}