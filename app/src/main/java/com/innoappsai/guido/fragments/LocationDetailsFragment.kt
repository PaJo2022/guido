package com.innoappsai.guido.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.adapters.ImageSliderAdapter
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.PlaceReviewAdapter
import com.innoappsai.guido.adapters.PlaceVideoAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.callToNumber
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentLocationDetailsBinding
import com.innoappsai.guido.makeTextViewClickableLink
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.openWebsite
import com.innoappsai.guido.toggleEnableAndVisibility
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceImages: ImageAdapter
    private lateinit var adapterPlaceReview: PlaceReviewAdapter
    private lateinit var adapterImageSlider: ImageSliderAdapter
    private lateinit var adapterPlaceVideos: PlaceVideoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceImages = ImageAdapter(requireContext())
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
        adapterImageSlider = ImageSliderAdapter(requireContext())
        adapterPlaceVideos = PlaceVideoAdapter()
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
        val snapHelper: SnapHelper = PagerSnapHelper()
        binding.apply {
            icArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
            rvPlaceReviews.apply {
                adapter = adapterPlaceReview
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            rvPlaceVideos.apply {
                adapter = adapterPlaceVideos
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                snapHelper.attachToRecyclerView(this)
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
                val videoUrls = it?.locationVideos?.map { videoUiModel ->
                    videoUiModel.videoUrl
                }
                adapterPlaceVideos.setPlaceVideos(videoUrls)
            }
            singlePlaceData.observe(viewLifecycleOwner) {
                binding.apply {

                    tvPlaceName.text = it?.name
                    tvPlaceName.isSelected = true
                    tvPlaceAddress.makeTextViewClickableLink(
                        it?.address,
                        errorMessage = "No Address Found"
                    ) {
                        openDirection(requireContext(), it?.name, it?.latLng)
                    }
                    tvPlaceMobile.makeTextViewClickableLink(
                        it?.callNumber, errorMessage = "No Phone Number Found"
                    ) {
                        requestCallPermissionAndMakeCall()
                    }
                    tvPlaceWebsite.makeTextViewClickableLink(
                        it?.website,
                        errorMessage = "No website found"
                    ) {
                        openWebsite(requireContext(), it?.website.toString())
                    }
                    llPlaceReviews.isVisible = !it?.reviews.isNullOrEmpty()
                }
                it?.photos?.let { photos ->
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

        adapterPlaceImages.setOnPhotoClicked { photoUrl ->

        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }


    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, you can make a phone call here
                viewModel.callNumber?.let {
                    callToNumber(
                        requireContext(),
                        it
                    )
                }
            } else {
                // Permission is denied, handle it as needed (e.g., show a message)
                // You may want to inform the user that the permission is required to make calls.
                requireContext().openAppSettings()
            }
        }


    // ...

    // When you want to make a call, call this function
    private fun requestCallPermissionAndMakeCall() {
        val permission = Manifest.permission.CALL_PHONE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, make the call
            viewModel.callNumber?.let {
                callToNumber(
                    requireContext(),
                    it
                )
            }
        } else {
            // Permission is not granted, request it
            requestCallPermissionLauncher.launch(permission)
        }
    }


}