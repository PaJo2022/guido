package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.CHIPS_VIEW
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentProfileNewBinding
import com.innoappsai.guido.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(requireContext(), CHIPS_VIEW)
        thumbView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_seekbar_thumb, null, false)
    }

    override fun onResume() {
        super.onResume()
        val currentDistanceInPref = appPrefs.prefDistance
        binding.seekbarDistance.progress = currentDistanceInPref / 1000
        binding.tvDistance.text = "${currentDistanceInPref / 1000} Km"
    }

    private fun OpenNavFragment(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvEditProfile.setOnClickListener {
                OpenNavFragment(
                    UserDetailsFragment(), parentFragmentManager, "UserFragment", binding.flId
                )
            }
            tvMyPlaces.setOnClickListener { OpenNavFragment(
                MyPlacesFragment(), parentFragmentManager, "MyPlacesFragment", binding.flId
            ) }
            icArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
            seekbarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvDistance.text = "$p1 Km"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                   viewModel.onDistanceChanged(p0?.progress ?: return)
                }

            })

            rvInterests.apply {
                adapter = placesTypeGroupAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            btnSaveChanges.setOnClickListener {
                viewModel.savePlaceTypePreferences()
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
                    Glide.with(requireContext()).load(it?.profilePicture)
                        .placeholder(R.drawable.ic_profile_img_placeholder)
                        .error(R.drawable.ic_profile_img_placeholder).centerCrop()
                        .into(circleImageView)
                }
            }
            userInterestes.observe(viewLifecycleOwner) {
                placesTypeGroupAdapter.setPlacesType(it)
            }
            isPlaceInterestesSaved.collectIn(viewLifecycleOwner) {
                sharedViewModel.onPreferencesSaved()
                parentFragmentManager.popBackStack()
            }
            newInterestsSelected.observe(viewLifecycleOwner) {
                binding.btnSaveChanges.isVisible = it
            }

        }
        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            homeViewModel.resetData()
            viewModel.onPlaceInterestClicked(it.id)

        }
        placesTypeGroupAdapter.setOnInterestSectionOpen{
           viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
               delay(100)
               withContext(Dispatchers.Main){
                   binding.nestedScrollView.fullScroll(View.FOCUS_DOWN)
               }
           }
        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

    }



}