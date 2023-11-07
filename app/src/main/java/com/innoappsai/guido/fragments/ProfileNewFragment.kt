package com.innoappsai.guido.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.auth.AuthActivity
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentProfileNewBinding
import com.innoappsai.guido.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileNewFragment : BaseFragment<FragmentProfileNewBinding>(FragmentProfileNewBinding::inflate) {

    private lateinit var thumbView: View
    private lateinit var viewModel: ProfileViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val auth by lazy { FirebaseAuth.getInstance() }

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        thumbView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_seekbar_thumb, null, false)
    }

    private fun openNavFragment(
        f: Fragment,
        args: Bundle? = null
    ) {
        FragmentUtils.replaceFragment((activity as MainActivity), binding.flId.id, f,args,true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvEditProfile.setOnClickListener {
                openNavFragment(
                    UserDetailsFragment()
                )
            }
            tvMyPlaces.setOnClickListener {
                openNavFragment(
                    MyPlacesFragment()
                )
            }
            tvMyItinerary.setOnClickListener {
                openNavFragment(
                    MyTravelItineraryListFragment()
                )
            }
            ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
            btnLogout.setOnClickListener {
                auth.signOut()
                viewModel.signOut()
                requireActivity().finish()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
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