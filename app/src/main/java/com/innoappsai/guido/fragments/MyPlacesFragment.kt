package com.innoappsai.guido.fragments


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesGroupListAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentMyPlacesBinding
import com.innoappsai.guido.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyPlacesFragment : BaseFragment<FragmentMyPlacesBinding>(FragmentMyPlacesBinding::inflate) {

    private val viewModel: MyPlacesVideModel by viewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            fetchPlacesUsingUserId()
            nearByPlacesInGroup.observe(viewLifecycleOwner) {
                binding.tvNoPlacesAdded.isVisible = it.isEmpty()
                placesAdapter.setNearByPlaces(it)
            }
        }
        placesAdapter.setOnLandMarkClicked {
            Bundle().apply {
                putParcelable("LANDMARK_DATA", it)
                openNavFragment(
                    LocationDetailsFragment(),
                    childFragmentManager,
                    "ProfileFragment",
                    binding.flId,
                    this
                )
            }
        }
        binding.apply {
            rvPlaces.apply {
                adapter = placesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }


    private fun openNavFragment(
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

}