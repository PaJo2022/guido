package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.guido.app.BaseFragment
import com.guido.app.adapters.CustomItemDecoration
import com.guido.app.adapters.ImageSliderAdapter
import com.guido.app.adapters.PlaceImageAdapter
import com.guido.app.adapters.PlaceReviewAdapter
import com.guido.app.addOnBackPressedCallback
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationDetailsBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.toggleEnableAndVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceImages: PlaceImageAdapter
    private lateinit var adapterPlaceReview: PlaceReviewAdapter
    private lateinit var adapterImageSlider: ImageSliderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceImages = PlaceImageAdapter(requireContext())
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
        adapterImageSlider = ImageSliderAdapter(requireContext())
    }

    private fun setUpViewPager() {
        binding.ivPlaceImage.adapter = adapterImageSlider
        binding.ivPlaceImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = 1
        binding.ivPlaceImage.currentItem = currentPageIndex
        TabLayoutMediator(binding.vpPlaceImageIndicator, binding.ivPlaceImage) { tab, position ->

        }.attach()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeUiModel = arguments?.getParcelable<PlaceUiModel>("LANDMARK_DATA")
        setUpViewPager()
        binding.apply {
            icArrowBack.setOnClickListener { parentFragmentManager.popBackStack()}
            rvPlaceReviews.apply {
                adapter = adapterPlaceReview
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        viewModel.apply {
            getSinglePlaceDetails(placeUiModel)
            getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel)
            placeUiModel?.let {
                fetchAllDataForTheLocation(it)
            }
            isPlaceDataFetching.collectIn(viewLifecycleOwner){
                binding.llPlaceData.toggleEnableAndVisibility(!it)
                binding.cbLcoationDataFetching.isVisible = it
            }
            isPlaceAIDataFetching.collectIn(viewLifecycleOwner) {
                binding.pbChatgptApiCalling.isVisible = it
                if (!it) {
                    val layoutParams = binding.tvPlaceDescription.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.tvPlaceDescription.layoutParams = layoutParams
                }
            }
            placeDistance.observe(viewLifecycleOwner) {
                binding.tvPlaceDistance.text = it
            }
            landMarkData.observe(viewLifecycleOwner) {
                binding.llPlaceVideos.isVisible = !it?.locationVideos.isNullOrEmpty()
                val webSettings = binding.wvPlaceVideos.settings
                webSettings.javaScriptEnabled = true

                // Load the YouTube video URL
                val videoUrl = it?.locationVideos?.firstOrNull()?.videoUrl.toString()
                binding.wvPlaceVideos.loadUrl(videoUrl)

                // Set a WebViewClient to handle redirects and other events
                binding.wvPlaceVideos.webViewClient = WebViewClient()
            }
            singlePlaceData.observe(viewLifecycleOwner) {
                binding.apply {

                    tvPlaceName.text = it?.name
                    tvPlaceName.isSelected = true
                    tvPlaceAddress.text = it?.address
                    tvPlaceMobile.text = it?.callNumber ?: "No Contact Number"
                    tvPlaceWebsite.text = it?.website ?: "No Website"
                    llPlaceReviews.isVisible = !it?.reviews.isNullOrEmpty()
                }
                it?.photos?.let { photos ->
                    adapterPlaceImages.setPlacePhotos(photos)
                    adapterImageSlider.setPlacePhotos(photos)
                }
                it?.reviews?.let {
                    adapterPlaceReview.setPlaceReviews(it)
                }

            }
            landMarkTourDataData.observe(viewLifecycleOwner) {
                binding.tvPlaceDescription.text = it
            }
        }

        adapterPlaceImages.setOnPhotoClicked {photoUrl->

        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }




}