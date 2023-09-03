package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.guido.app.BaseFragment
import com.guido.app.Constants
import com.guido.app.MyApp
import com.guido.app.adapters.PlacesTypeChipAdapter
import com.guido.app.adapters.VerticalGridCustomItemDecoration
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate){


    private lateinit var viewModel : ProfileViewModel
    private lateinit var placesTypeChipAdapter : PlacesTypeChipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        placesTypeChipAdapter = PlacesTypeChipAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApp.userCurrentFormattedAddress?.apply {
            binding.etUserAddress.setText(this)
        }
        binding.apply {
            profileImage.setOnClickListener {
                findNavController().popBackStack()
            }
            rvInterests.apply {
                addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                adapter = placesTypeChipAdapter
                layoutManager =
                    GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
            }
            btnSave.setOnClickListener {
                    viewModel.savePlaceTypePreferences()
                    findNavController().popBackStack()
                }
            }
        viewModel.apply {
            userInterestes.observe(viewLifecycleOwner){
                placesTypeChipAdapter.setPlacesType(it)
            }

        }
        placesTypeChipAdapter.setOnPlaceTypeSelected {
            viewModel.onPlaceInterestClicked(it.id)
        }

    }




}