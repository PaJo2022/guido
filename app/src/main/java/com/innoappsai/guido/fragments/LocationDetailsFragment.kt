package com.innoappsai.guido.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.adapters.DividerItemDecoration
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.ImageSliderAdapter
import com.innoappsai.guido.adapters.PlaceMoreInfoAdapter
import com.innoappsai.guido.adapters.PlaceReviewAdapter
import com.innoappsai.guido.adapters.PlaceVideoAdapter
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.callToNumber
import com.innoappsai.guido.databinding.FragmentLocationDetailsNewBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.makeTextViewClickableLink
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.openDirection
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsNewBinding>(FragmentLocationDetailsNewBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceImages: ImageAdapter
    private lateinit var adapterPlaceReview: PlaceReviewAdapter
    private lateinit var adapterImageSlider: ImageSliderAdapter
    private lateinit var adapterPlaceVideos: PlaceVideoAdapter
    private lateinit var adapterVideos: VideoAdapter
    private lateinit var adapterPlaceExtraInfo: PlaceMoreInfoAdapter

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceImages = ImageAdapter(requireContext())
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
        adapterImageSlider = ImageSliderAdapter(requireContext())
        adapterPlaceVideos = PlaceVideoAdapter()
        adapterPlaceExtraInfo = PlaceMoreInfoAdapter()
        adapterVideos = VideoAdapter(requireContext())
    }

    private fun setUpViewPager() {
        binding.ivPlaceImage.adapter = adapterImageSlider
        binding.ivPlaceImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = 1
        binding.ivPlaceImage.currentItem = currentPageIndex
        TabLayoutMediator(binding.vpPlaceImageIndicator, binding.ivPlaceImage) { tab, position ->

        }.attach()

        binding.viewPagerVideos.adapter = adapterVideos
        binding.viewPagerVideos.orientation = ViewPager2.ORIENTATION_HORIZONTAL


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetFragment = BottomPlaceOptions()
        bottomSheetFragment.setOnSuccessFullPlaceDeleted {
            parentFragmentManager.popBackStack()
        }
        val palceId = arguments?.getString("PLACE_ID")
        setUpViewPager()
        binding.apply {
            ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
            rvReviews.apply {
                adapter = adapterPlaceReview
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            rvPlaceExtraDetails.apply {
                addItemDecoration(DividerItemDecoration(requireContext()))
                adapter = adapterPlaceExtraInfo
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        viewModel.apply {
            getSinglePlaceDetails(palceId)
//            isPlaceDataFetching.collectIn(viewLifecycleOwner){
//                binding.llPlaceData.toggleEnableAndVisibility(!it)
//                binding.cbLcoationDataFetching.isVisible = it
//            }
//            isPlaceAIDataFetching.collectIn(viewLifecycleOwner) {
//                binding.pbChatgptApiCalling.isVisible = it
//                if (!it) {
//                    val layoutParams = binding.tvAboutThePlace.layoutParams
//                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
//                    binding.tvAboutThePlace.layoutParams = layoutParams
//                }
//            }
            placeDistance.observe(viewLifecycleOwner) {
                binding.tvPlaceDistance.text = it
            }
            landMarkData.observe(viewLifecycleOwner) {
//                binding.llPlaceYoutubeVideos.isVisible = !it?.locationVideos.isNullOrEmpty()
//                val videoUrls = it?.locationVideos?.map { videoUiModel ->
//                    videoUiModel.videoUrl
//                }
//                adapterPlaceVideos.setPlaceVideos(videoUrls)
            }
            placeMoreData.observe(viewLifecycleOwner) {
                adapterPlaceExtraInfo.setPlaceExtraInfo(it)
            }
            singlePlaceData.observe(viewLifecycleOwner) { placeUiModel ->
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

                    placeOpeningStatus.text = placeUiModel?.placeOpenStatus
                    tvPlaceName.text = placeUiModel?.name
                    tvPlaceName.isSelected = true
                    Glide.with(requireContext()).load(placeUiModel?.placeMapImage).centerCrop()
                        .into(binding.placeMapImage)
                    tvPlaceAddress.makeTextViewClickableLink(
                        placeUiModel?.address,
                        errorMessage = "No Address Found"
                    ) {
                        openDirection(requireContext(), placeUiModel?.name, placeUiModel?.latLng)
                    }

                }
                placeUiModel?.photos?.let { photos ->
                    adapterImageSlider.setPlacePhotos(photos)
                }
                placeUiModel?.placeDescription?.let {
                    binding.tvPlaceDescription.text = it
                }
                placeUiModel?.videos?.let { videos ->
                    adapterVideos.setVideos(ArrayList(videos.map { Uri.parse(it) }))
                }
                placeUiModel?.reviews?.let {
                    adapterPlaceReview.setPlaceReviews(it)
                }

            }
            landMarkTourDataData.observe(viewLifecycleOwner) {

            }
        }


        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

        adapterPlaceVideos.setOnFullScreenClickListener{videoUrl->

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            intent.setPackage("com.google.android.youtube")

            // Verify that the YouTube app is installed on the device
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // If the YouTube app is not installed, open in a web browser
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                startActivity(webIntent)
            }

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