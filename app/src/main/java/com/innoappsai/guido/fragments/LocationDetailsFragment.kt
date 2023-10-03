package com.innoappsai.guido.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.ImageSliderAdapter
import com.innoappsai.guido.adapters.PlaceReviewAdapter
import com.innoappsai.guido.adapters.PlaceVideoAdapter
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.callToNumber
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentLocationDetailsBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.makeTextViewClickableLink
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.openWebsite
import com.innoappsai.guido.toggleEnableAndVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceImages: ImageAdapter
    private lateinit var adapterPlaceReview: PlaceReviewAdapter
    private lateinit var adapterImageSlider: ImageSliderAdapter
    private lateinit var adapterPlaceVideos: PlaceVideoAdapter
    private lateinit var adapterVideos: VideoAdapter
    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceImages = ImageAdapter(requireContext())
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
        adapterImageSlider = ImageSliderAdapter(requireContext())
        adapterPlaceVideos = PlaceVideoAdapter()
        adapterVideos = VideoAdapter(requireContext())
    }

    private fun setUpViewPager() {
        binding.ivPlaceImage.adapter = adapterImageSlider
        binding.ivPlaceImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = 1
        binding.ivPlaceImage.currentItem = currentPageIndex
        TabLayoutMediator(binding.vpPlaceImageIndicator, binding.ivPlaceImage) { tab, position ->

        }.attach()

        binding.rvPlaceVideos.adapter = adapterVideos
        binding.rvPlaceVideos.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetFragment = BottomPlaceOptions()
        bottomSheetFragment.setOnSuccessFullPlaceDeleted {
            parentFragmentManager.popBackStack()
        }
        val placeUiModel = arguments?.getParcelable<PlaceUiModel>("LANDMARK_DATA")
        setUpViewPager()
        binding.apply {
            icArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
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
                    val layoutParams = binding.tvAboutThePlace.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.tvAboutThePlace.layoutParams = layoutParams
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
            singlePlaceData.observe(viewLifecycleOwner) {placeUiModel->
                binding.apply {
                    ivPlaceOptions.apply {
                        isVisible = placeUiModel?.createdBy == appPrefs.userId
                        setOnClickListener {

                            val bundle = Bundle()
                            bundle.putParcelable("PLACE_DATA", placeUiModel)
                            bottomSheetFragment.arguments = bundle
                            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                        }
                    }
                    tvPlaceName.text = placeUiModel?.name
                    tvPlaceName.isSelected = true
                    tvPlaceAddress.makeTextViewClickableLink(
                        placeUiModel?.address,
                        errorMessage = "No Address Found"
                    ) {
                        openDirection(requireContext(), placeUiModel?.name, placeUiModel?.latLng)
                    }
                    tvPlaceMobile.makeTextViewClickableLink(
                        placeUiModel?.callNumber, errorMessage = "No Phone Number Found"
                    ) {
                        requestCallPermissionAndMakeCall()
                    }
                    tvPlaceWebsite.makeTextViewClickableLink(
                        placeUiModel?.website,
                        errorMessage = "No website found"
                    ) {
                        openWebsite(requireContext(), placeUiModel?.website.toString())
                    }
                    llPlaceReviews.isVisible = !placeUiModel?.reviews.isNullOrEmpty()
                }
                placeUiModel?.photos?.let { photos ->
                    adapterImageSlider.setPlacePhotos(photos)
                }
                placeUiModel?.placeDescription?.let {
                    binding.llAboutThePlace.isVisible = true
                    binding.tvPlaceDescription.text = it
                }
                placeUiModel?.videos?.let { videos ->
                    adapterVideos.setVideos(
                        ArrayList(videos.map { video -> Uri.parse(video) })
                    )
                }
                placeUiModel?.reviews?.let {
                    adapterPlaceReview.setPlaceReviews(it)
                }

            }
            landMarkTourDataData.observe(viewLifecycleOwner) {
                binding.tvAboutThePlace.text = it
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

    override fun onDestroy() {
        super.onDestroy()
        adapterVideos.releasePlayers()
    }

}