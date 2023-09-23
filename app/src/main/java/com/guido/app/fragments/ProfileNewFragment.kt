package com.guido.app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.adapters.PlacesTypeGroupAdapter
import com.guido.app.adapters.VerticalGridCustomItemDecoration
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentProfileBinding
import com.guido.app.databinding.FragmentProfileNewBinding
import com.guido.app.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileNewFragment : BaseFragment<FragmentProfileNewBinding>(FragmentProfileNewBinding::inflate) {

    private lateinit var thumbView: View
    private lateinit var viewModel: ProfileViewModel
    private lateinit var placesTypeGroupAdapter: PlacesTypeGroupAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(requireContext())
        thumbView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_seekbar_thumb, null, false)
    }

    override fun onResume() {
        super.onResume()
        val currentDistanceInPref = appPrefs.prefDistance
        binding.seekbarDistance.progress = currentDistanceInPref / 1000
        binding.tvDistance.text = "${currentDistanceInPref / 1000} Km"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvEditProfile.setOnClickListener { findNavController().navigate(R.id.userDetailsFragment) }
            seekbarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvDistance.text = "$p1 Km"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    viewModel.distanceProgress = p0?.progress ?: return

                }

            })

            rvInterests.apply {
                addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                adapter = placesTypeGroupAdapter
                layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.VERTICAL,false)
            }
        }

        viewModel.apply {
            getSavedPreferences().collectIn(viewLifecycleOwner){
                binding.tvUserIntrestes.text = it.size.toString()
            }
            getUserData().collectIn(viewLifecycleOwner) {
                binding.apply {
                    tvUserName.text = it?.displayName ?: "Awesome Usr"
                    tvUserLocation.text = it?.location ?: "No Location"
                    tvUserLocation.isSelected = true
                    Glide.with(requireContext()).load(it?.profilePicture).centerCrop().into(ivProfilePicture)
                }
            }
            userInterestes.observe(viewLifecycleOwner) {
                placesTypeGroupAdapter.setPlacesType(it)
            }
            isPlaceInterestesSaved.collectIn(viewLifecycleOwner){
                sharedViewModel.onPreferencesSaved()
            }

        }
        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            homeViewModel.resetData()
            viewModel.onPlaceInterestClicked(it.id)

        }

    }


}