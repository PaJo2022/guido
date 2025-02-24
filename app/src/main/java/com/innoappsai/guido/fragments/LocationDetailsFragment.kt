package com.innoappsai.guido.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.DividerItemDecoration
import com.innoappsai.guido.adapters.ImageSliderAdapter
import com.innoappsai.guido.adapters.PlaceMoreInfoAdapter
import com.innoappsai.guido.adapters.PlaceReviewAdapter
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.callToNumber
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentLocationDetailsBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateStaticMapUrl
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.openWebsite
import com.innoappsai.guido.toggleEnableAndAlpha
import com.innoappsai.guido.toggleEnableAndVisibility
import com.innoappsai.guido.workers.DownloadImageWorker
import com.innoappsai.guido.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceReview: PlaceReviewAdapter
    private lateinit var adapterImageSlider: ImageSliderAdapter
    private lateinit var adapterVideos: VideoAdapter
    private lateinit var adapterPlaceExtraInfo: PlaceMoreInfoAdapter
    private lateinit var workManager: WorkManager

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
        adapterImageSlider = ImageSliderAdapter(requireContext())
        adapterPlaceExtraInfo = PlaceMoreInfoAdapter()
        adapterVideos = VideoAdapter(requireContext())
        workManager = WorkManager.getInstance(requireContext())
    }

    private fun setUpViewPager() {
        binding.vpPlaceImage.adapter = adapterImageSlider
        binding.vpPlaceImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        val currentPageIndex = 1
        binding.vpPlaceImage.currentItem = currentPageIndex
        TabLayoutMediator(binding.vpPlaceImageIndicator, binding.vpPlaceImage) { tab, position ->

        }.attach()

        binding.viewPagerVideos.adapter = adapterVideos
        binding.viewPagerVideos.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        TabLayoutMediator(binding.vpPlaceVideosIndicator, binding.viewPagerVideos) { tab, position ->

        }.attach()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetFragment = BottomPlaceOptions()
        bottomSheetFragment.setOnSuccessFullPlaceDeleted {
            parentFragmentManager.popBackStack()
        }
        val placeId = arguments?.getString("PLACE_ID")
        setUpViewPager()
        binding.apply {
            swipeRefreshLayout.isEnabled = false
//            ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
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
            fetchDetailsById(placeId)
            isPlaceDataFetching.collectIn(viewLifecycleOwner) {
                binding.apply {
                    llLocationPrimaryDetails.root.toggleEnableAndVisibility(!it)
                    llLocationPrimaryDetailsShimmer.root.toggleEnableAndVisibility(it)
                }
                binding.swipeRefreshLayout.isRefreshing = it
            }
            isPlaceAIDataFetching.collectIn(viewLifecycleOwner) {
                binding.tvPlaceShimmerLayout.isVisible = it
                if (!it) {
                    val layoutParams = binding.tvPlaceDescription.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    binding.tvPlaceDescription.layoutParams = layoutParams
                }
            }
            placeDistance.observe(viewLifecycleOwner) {
                binding.llLocationPrimaryDetails.tvPlaceDistance.text = it
            }
            landMarkVideoData.observe(viewLifecycleOwner) {
                binding.tvPlaceVideos.isVisible = it.isNotEmpty()
                binding.viewPagerVideos.isVisible = it.isNotEmpty()
                binding.vpPlaceVideosIndicator.isVisible = it.isNotEmpty()
                adapterVideos.setVideos(ArrayList(it))
            }
            placeMoreData.observe(viewLifecycleOwner) {
                adapterPlaceExtraInfo.setPlaceExtraInfo(it)
            }
            singlePlaceData.observe(viewLifecycleOwner) { placeUiModel ->
                setPlacePricingType(placeUiModel?.pricingType)
                binding.ivCall.toggleEnableAndAlpha(placeUiModel?.callNumber != null)
                binding.ivWebsite.toggleEnableAndAlpha(placeUiModel?.website != null)
                binding.ratingBarForPlace.setOnRatingBarChangeListener { ratingBar, fl, b ->
                    Bundle().apply {
                        putFloat("PLACE_RATING", fl)
                        putString("PLACE_DB_ID", placeUiModel?.serverDbId)
                        openNavFragment(
                            AddReviewFragment(),
                            childFragmentManager,
                            "ProfileFragment",
                            binding.mainLayout,
                            this
                        )
                    }
                }
                binding.apply {
                    ivWebsite.setOnClickListener {
                        placeUiModel?.website?.let { it1 -> openWebsite(requireContext(), it1) }
                    }
                    ivCall.setOnClickListener {
                        requestCallPermissionAndMakeCall()
                    }
                    ivAddReview.setOnClickListener {
                        Bundle().apply {
                            putString("PLACE_DB_ID", placeUiModel?.serverDbId)
                            openNavFragment(
                                AddReviewFragment(),
                                childFragmentManager,
                                "ProfileFragment",
                                binding.mainLayout,
                                this
                            )
                        }
                    }
                    ivAddPhoto.setOnClickListener {
                       //Add Photo Logic
                    }
//                    ivPlaceOptions.apply {
//                        isVisible = placeUiModel?.createdBy == appPrefs.userId
//                        setOnClickListener {
//                            val bundle = Bundle()
//                            bundle.putParcelable("PLACE_DATA", placeUiModel)
//                            bottomSheetFragment.arguments = bundle
//                            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
//                        }
//                    }
                    ivAddPhoto.toggleEnableAndAlpha(placeUiModel?.createdBy == appPrefs.userId)
                    llLocationPrimaryDetails.placeOpeningStatus.text = placeUiModel?.placeOpenStatus ?: "Closed"
                    llLocationPrimaryDetails.tvPlaceName.text = placeUiModel?.name
                    llLocationPrimaryDetails.tvPlaceName.isSelected = true
                    llLocationPrimaryDetails.placeRating.rating = placeUiModel?.rating?.toFloat() ?: 0f
                    llLocationPrimaryDetails.placeRatingText.text = "(${placeUiModel?.reviewsCount ?: 0})"
                    Glide.with(requireContext()).load(
                        placeUiModel?.placeMapImage ?: generateStaticMapUrl(
                            latitude = placeUiModel?.latLng?.latitude ?: 0.0,
                            longitude = placeUiModel?.latLng?.longitude ?:0.0
                        )
                    ).centerCrop()
                        .into(binding.placeMapImage)
                    tvPlaceAddress.text = placeUiModel?.address
                    placeMapImage.setOnClickListener {
                        openDirection(requireContext(), placeUiModel?.name, placeUiModel?.latLng)
                    }

                }
                placeUiModel?.photos?.let { photos ->
                    adapterImageSlider.setPlacePhotos(photos)
                }
                placeUiModel?.placeDescription?.let {
                    binding.tvPlaceDescription.text = it
                }


            }
            landMarkTourDataData.observe(viewLifecycleOwner) {
                binding.tvPlaceDescription.text = it
            }
            placeReviews.observe(viewLifecycleOwner) {
                binding.tvNoReviewsYet.isVisible = it.isNullOrEmpty()
                adapterPlaceReview.setPlaceReviews(it)
            }
        }


        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
        adapterPlaceReview.setOnReviewImageClicked {
            Bundle().apply {
                putParcelable("PLACE_REVIEW_DATA", it)
                openNavFragment(
                    ReviewMediaListFragment(),
                    childFragmentManager,
                    "ReviewMediaListFragment",
                    binding.mainLayout,
                    this
                )
            }
        }


        adapterVideos.setOnFullScreenClickListener { videoUrl ->
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
        adapterPlaceExtraInfo.setOnPaceExtraInfoClicked {
            openWebsite(requireContext(), it)
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

    private fun setPlacePricingType(pricingType : String?){
        val numberOfDollars = if(pricingType.equals("Expensive",true)) "$$$" else if(pricingType.equals("Moderate",true)) "$$" else if(pricingType.equals("Inexpensive",true)) "$" else ""
        binding.llLocationPrimaryDetails.placePricingType.text = numberOfDollars
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


    override fun onDestroy() {
        super.onDestroy()
        adapterVideos.releasePlayers()
    }

}