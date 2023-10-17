package com.innoappsai.guido.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentProfileNewBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.services.HyperLocalPlacesSearchService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileNewFragment : BaseFragment<FragmentProfileNewBinding>(FragmentProfileNewBinding::inflate) {

    private lateinit var thumbView: View
    private lateinit var viewModel: ProfileViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        thumbView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_seekbar_thumb, null, false)
    }

    private fun OpenNavFragment(
        f: Fragment,
        fm: FragmentManager,
        fragmentName: String,
        view: View,
        args: Bundle? = null
    ) {
        val ft = fm.beginTransaction()

        // Pass the bundle as arguments to the fragment, if provided
        if (args != null) {
            f.arguments = args
        }
        ft.setCustomAnimations(
            R.anim.in_from_right,
            R.anim.out_to_left,
            R.anim.in_from_left,
            R.anim.out_to_right
        )
        ft.replace(view.id, f, fragmentName).addToBackStack(fragmentName).commit()
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
            ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
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

            isPlaceInterestesSaved.collectIn(viewLifecycleOwner) {
                sharedViewModel.onPreferencesSaved()
                parentFragmentManager.popBackStack()
            }


        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

    }



}