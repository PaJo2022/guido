package com.innoappsai.guido.fragments


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.adapters.AdapterItineraryList
import com.innoappsai.guido.adapters.PlacesGroupListAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentMyPlacesBinding
import com.innoappsai.guido.databinding.FragmentMyTravelItineraryListBinding
import com.innoappsai.guido.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MyTravelItineraryListFragment :
    BaseFragment<FragmentMyTravelItineraryListBinding>(FragmentMyTravelItineraryListBinding::inflate) {

    private val viewModel: ViewModelMyTravelItineraryList by viewModels()
    private lateinit var adapterItineraryList: AdapterItineraryList

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterItineraryList = AdapterItineraryList(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            getListOfGeneratedItineraryList()
            itineraryList.observe(viewLifecycleOwner){
                binding.tvNoItineraryListFound.isVisible = it.isEmpty()
                adapterItineraryList.setItineraryList(it)
            }
        }
        adapterItineraryList.setOnItemClicked {
//            Bundle().apply {
//                FragmentUtils.replaceFragment(
//                    (requireActivity() as MainActivity), binding.flId.id, LocationDetailsFragment(),
//                    this, true
//                )
//            }
        }
        binding.apply {
            ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
            rvItineraryList.apply {
                adapter = adapterItineraryList
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }


}