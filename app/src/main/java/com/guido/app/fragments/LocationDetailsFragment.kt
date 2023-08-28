package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.guido.app.BaseFragment
import com.guido.app.adapters.TabAdapter
import com.guido.app.databinding.FragmentLocationDetailsBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private var vpLandmarkImages: TabAdapter? = null
    private var vpLandmarkStories: TabAdapter? = null
    private var vpLandmarkVideos: TabAdapter? = null

    private var videoFragmentList: List<Fragment> = emptyList()
    private var imageFragmentList: List<Fragment> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        vpLandmarkImages = TabAdapter(childFragmentManager, lifecycle)
        vpLandmarkStories = TabAdapter(childFragmentManager, lifecycle)
        vpLandmarkVideos = TabAdapter(childFragmentManager, lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeUiModel = arguments?.getParcelable<PlaceUiModel>("LANDMARK_DATA")
        binding.apply {
            viewpagerLandmarkVideos.adapter = vpLandmarkVideos
        }
        viewModel.apply {
            setPlaceData(placeUiModel)
            placeUiModel?.name?.let { fetchAllDataForTheLocation(it) }
            landMarkData.observe(viewLifecycleOwner) {
                videoFragmentList = it?.locationVideos?.map { videoUiModel ->
                    LandMarkVideoItemFragment.newInstance(videoUiModel)
                } ?: emptyList()
                vpLandmarkVideos?.setFragmentsItems(videoFragmentList)
                imageFragmentList = it?.placeUiModel?.photos?.map { image ->
                    LandMarkImageItemFragment.newInstance(image)
                } ?: emptyList()
            }
        }

    }

}