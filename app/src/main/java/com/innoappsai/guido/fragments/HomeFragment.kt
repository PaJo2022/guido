package com.innoappsai.guido.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.Constants.iconResourceMapping
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlaceFilterHorizontalAdapter
import com.innoappsai.guido.adapters.PlaceFilterHorizontalItemDecorator
import com.innoappsai.guido.adapters.PlaceVerticalItemDecorator
import com.innoappsai.guido.adapters.PlacesGroupListAdapter
import com.innoappsai.guido.adapters.PlacesHorizontalListAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.addplace.AddPlaceActivity
import com.innoappsai.guido.calculateDistance
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentHomeBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.generateItinerary.GenerateItineraryActivity
import com.innoappsai.guido.getScreenHeight
import com.innoappsai.guido.isServiceRunning
import com.innoappsai.guido.isVisibleAndEnable
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.PlaceFilter.PlaceFilterType
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.placeFilter.FilterActivity
import com.innoappsai.guido.services.HyperLocalPlacesSearchService
import com.innoappsai.guido.showToast
import com.innoappsai.guido.workers.CreateItineraryGeneratorWorker
import com.innoappsai.guido.workers.WorkerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    OnMapReadyCallback, OnMarkerClickListener, GoogleMap.OnInfoWindowCloseListener,
    GoogleMap.OnCameraMoveListener {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "mapview_bundle_key"
    }


    private var bottomSheetFragment: BottomPlaceSortOptions? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter
    private lateinit var placesHorizontalAdapter: PlacesHorizontalListAdapter
    private lateinit var placeFilterAdapter: PlaceFilterHorizontalAdapter
    private var googleMap: GoogleMap? = null
    private var doubleBackToExitPressedOnce = false
    private var bottomHyperPlaceSearch: BottomHyperPlaceSearch? = null
    private var bottomSheetTwoButtonHyperPlaceSearch: BottomTwoButton? = null

    private val zoom = 13f

    @Inject
    lateinit var appPrefs: AppPrefs

    private fun observeWorkers() {
        CreateItineraryGeneratorWorker.workerState.observe(viewLifecycleOwner) { workState ->
            binding.llItineraryIsAdded.root.isVisible = workState == WorkerState.COMPLETE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        placesHorizontalAdapter = PlacesHorizontalListAdapter(requireContext())
        placeFilterAdapter = PlaceFilterHorizontalAdapter(requireContext())
        checkLocationPermission()
        bottomHyperPlaceSearch = BottomHyperPlaceSearch()
        bottomSheetTwoButtonHyperPlaceSearch = BottomTwoButton(
            title = "Stop Hyper Local Search",
            description = "Do You Want To Stop Hyper Local Search?",
            onPositiveButtonClick = {
                val serviceIntent =
                    Intent(requireContext(), HyperLocalPlacesSearchService::class.java)
                requireActivity().stopService(serviceIntent)
                bottomSheetTwoButtonHyperPlaceSearch?.dismiss()
            },
            onNegetiveButtonClick = {
                bottomSheetTwoButtonHyperPlaceSearch?.dismiss()
            }
        )
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                AddPlaceActivity.startAddPlaceActivity(requireContext())
            } else {
                requestPermissionLauncher.launch(permission)
            }
        } else {
            AddPlaceActivity.startAddPlaceActivity(requireContext())
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                AddPlaceActivity.startAddPlaceActivity(requireContext())
            } else {
                Bundle().apply {
                    putBoolean("SHOULD_GO_TO_SETTINGS", true)
                    findNavController().navigate(R.id.bottomSheetAskLocationPermission, this)
                }
            }
        }


    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted && coarseLocationGranted) {
                // Both permissions are granted
                // Get the current location
                viewModel.fetchCurrentLocation(true)
            } else {
                // Handle the denied or permanently denied cases for either permission
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    // Permission is permanently denied (user selected "Never ask again")
                    // You may inform the user or redirect them to app settings
                   requireActivity().openAppSettings()
                } else {
                    // Permission is denied (user selected "Deny" but not "Never ask again")
                    // You can handle this case by showing a rationale
                    requireActivity().showToast("Please allow location permission show we can help you with more personalzied travel expereince")
                }
            }
        }

    private fun checkLocationPermission(shouldAnimate: Boolean = false) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted && coarseLocationGranted) {
            // Both permissions are already granted
            // Get the current location
            viewModel.fetchCurrentLocation(shouldAnimate)
            googleMap?.isMyLocationEnabled = true
        } else {
            // Permission(s) is/are not yet granted, request them
            val permissionsToRequest = mutableListOf<String>()
            if (!fineLocationGranted) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (!coarseLocationGranted) {
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            locationPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun openNavFragment(
        f: Fragment,
        args: Bundle? = null
    ) {
        FragmentUtils.replaceFragment((activity as MainActivity), binding.flId.id, f,args,true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("DEEPLINK")?.let {
            if (it.equals("PLACE_ITINERARY_SCREEN", false)) {
                navigateToGeneratedItinerary()
            } else if (it.equals("PLACE_DETAILS_SCREEN", false)) {
                val placeId = arguments?.getString("PLACE_ID") ?: return
                navigateToPlaceDetailsScreen(placeId)
            }

        }

        val snapHelper1: SnapHelper = PagerSnapHelper()
        val placeCardHorizontalLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.apply {
            llItineraryIsAdded.root.setOnClickListener {
                navigateToGeneratedItinerary()
            }
            swipeRefreshLayout.isEnabled = false
            bottomsheetPlaceList.btnAddNewPlace.setOnClickListener {
                askForNotificationPermission()
            }
            bottomsheetPlaceList.rvPlaces.apply {
                addItemDecoration(PlaceVerticalItemDecorator(requireContext()))
                adapter = placesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            bottomsheetPlaceList.rvFilters.apply {
                addItemDecoration(PlaceFilterHorizontalItemDecorator(requireContext()))
                adapter = placeFilterAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            binding.bottomsheetPlaceList.llLocateMe.root.setOnClickListener {
                checkLocationPermission(shouldAnimate = true)
            }
            tvLastSearchLocation.setOnClickListener {
                openNavFragment(
                    SearchLocationFragment(),
                )
            }

            rvPlaceCards.apply {
                adapter = placesHorizontalAdapter
                layoutManager = placeCardHorizontalLayoutManager
            }
            ivUserProfileImage.setOnClickListener {
                openNavFragment(
                    ProfileNewFragment()
                )
            }
            bottomsheetPlaceList.apply {
                ivCloseGenerateItineraryLayout.setOnClickListener {
                    viewModel.onItineraryGenerationCancelledClicked()
                }
                btnGenerate.setOnClickListener {
                    viewModel.onItineraryGenerationCancelledClicked(isForceCancel = false)
                    Bundle().apply {
                        putString("PLACE_ADDRESS", MyApp.userCurrentFormattedAddress)
                        putString("PLACE_COUNTRY", MyApp.currentCountry)
                        openNavFragment(
                            FragmentPlaceGenerateItinerary()
                        )
                    }
                }
            }

            snapHelper1.attachToRecyclerView(binding.rvPlaceCards)
        }

        binding.rvPlaceCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView: View =
                        snapHelper1.findSnapView(placeCardHorizontalLayoutManager) ?: return
                    val pos: Int = placeCardHorizontalLayoutManager.getPosition(centerView)
                    viewModel.setThePositionForHorizontalPlaceAdapter(pos)
                }
            }
        })


        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomsheetPlaceList.bottomSheet)


        val screenHeight = requireContext().getScreenHeight()
        val peekHeight1 = (screenHeight * 0.18).roundToInt()
        bottomSheetBehavior.peekHeight = peekHeight1
        val margin = (screenHeight * 0.10).roundToInt()
        val maxHeight = (screenHeight * 0.65).roundToInt()


        bottomSheetBehavior.maxHeight = maxHeight

        bottomSheetBehavior.isHideable = false


        val layoutParams = binding.rvPlaceCards.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = margin
        binding.rvPlaceCards.layoutParams = layoutParams

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet is fully expanded
                        googleMap?.setPadding(
                            0,
                            0,
                            0,
                            (requireContext().getScreenHeight() * 0.50).toInt()
                        )
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                viewModel.lastSearchLocationLatLng!!, zoom
                            )
                        )
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        googleMap?.setPadding(
                            0,
                            0,
                            0,
                            0
                        )
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                viewModel.lastSearchLocationLatLng!!, zoom
                            )
                        )
                        binding.bottomsheetPlaceList.rvPlaces.scrollToPosition(0)
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // BottomSheet is hidden
                    }
                    // You can handle other states as needed
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // This method is called when the BottomSheet is being dragged or settled
                // You can perform actions based on the slideOffset if needed
            }
        })


        observeData()
        observeWorkers()

        placeFilterAdapter.setOnFilterItemClicked{placeFilter->
            if(placeFilter.placeFilterType != PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH){
                viewModel.onFilterOptionClicked(placeFilter.placeFilterType)
            }
            when(placeFilter.placeFilterType){
                PlaceFilterType.FULL_FILTER -> {
                    startActivity(Intent(requireContext(), FilterActivity()::class.java))
                }

                PlaceFilterType.UNLOCK_FILTERS -> {

                }

                PlaceFilterType.SORT -> {
                    bottomSheetFragment = BottomPlaceSortOptions()
                    bottomSheetFragment?.show(childFragmentManager, bottomSheetFragment?.tag)
                    bottomSheetFragment?.setOnSortOptionSelected{
                        bottomSheetFragment = null
                        viewModel.sortOptionSelected(it)

                    }
                }

                PlaceFilterType.OPEN_NOW -> {
                    viewModel.onOpenNowFilterClicked()
                }

                PlaceFilterType.MORE_FILTERS -> {
                    startActivity(Intent(requireContext(), FilterActivity()::class.java))
                }

                PlaceFilterType.TRAVEL_ITINERARY -> {
                    GenerateItineraryActivity.startActivity(requireContext())
//                    if (MyApp.userCurrentFormattedAddress != null) {
//                        viewModel.onItineraryGenerationClicked()
//                    } else {
//
//                    }
                }

                PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH -> {

                    if (isServiceRunning(
                            requireContext(),
                            HyperLocalPlacesSearchService::class.java
                        )
                    ) {
                        bottomSheetTwoButtonHyperPlaceSearch?.show(
                            childFragmentManager,
                            bottomSheetTwoButtonHyperPlaceSearch?.tag
                        )
                    } else {
                        bottomHyperPlaceSearch?.show(
                            childFragmentManager,
                            bottomHyperPlaceSearch?.tag
                        )

                    }
                }
            }
        }

        addOnBackPressedCallback {
            if (doubleBackToExitPressedOnce) {
                requireActivity().finish()
            }
            this.doubleBackToExitPressedOnce = true
            requireActivity().showToast("Press back again to close the app")
            Handler().postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000) // 2000 milliseconds or 2 seconds
        }


    }

    fun navigateToGeneratedItinerary() {
        Bundle().apply {
            putString("ITINERARY_DB_ID", CreateItineraryGeneratorWorker.itineraryDbId)
            openNavFragment(
                FragmentPlaceItinerary()
            )
        }
        viewModel.onItineraryGenerationCancelledClicked()
        CreateItineraryGeneratorWorker.onObserved()
    }

    fun navigateToPlaceDetailsScreen(placeId: String) {
        Bundle().apply {
            putString("PLACE_ID", placeId)
            openNavFragment(
                LocationDetailsFragment()
            )
        }
    }

    private fun setLocationMarkers(placeUiModel: PlaceUiModel) {
        val markerUrl = placeUiModel.icon
        val markerLatLng = placeUiModel.latLng
        val landMarkName = placeUiModel.name
        val resourceId = iconResourceMapping[placeUiModel.icon]
        if (markerLatLng == null || landMarkName == null || resourceId == null) return


        val iconBitmap =
            ContextCompat.getDrawable(requireContext(), resourceId)!!.toBitmap()
        val markerOptions = MarkerOptions()
            .position(markerLatLng)
            .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
            .title(landMarkName)
        val marker = googleMap?.addMarker(markerOptions)
        marker?.let {
            viewModel.markerDataList.add(MarkerData(it, placeUiModel))
        }

    }

    private fun observeData() {
        viewModel.apply {
            selectedFilters.observe(viewLifecycleOwner) {
                placeFilterAdapter.setFilters(it)
            }
            selectedPlaces.observe(viewLifecycleOwner) {
                binding.bottomsheetPlaceList.tvSelectedPlaces.text =
                    "${it.size} Landmarks are selected"
            }
            showItineraryGenerationLayout.observe(viewLifecycleOwner) {
                val screenHeight = requireContext().getScreenHeight()
                binding.bottomsheetPlaceList.llPickLandmarks.isVisible = it
                if (it) {
                    val peekHeight = (screenHeight * 0.15).roundToInt()
                    bottomSheetBehavior.peekHeight = peekHeight
                    val maxHeight = (screenHeight * 0.85).roundToInt()
                    bottomSheetBehavior.maxHeight = maxHeight
                } else {
                    val peekHeight = (screenHeight * 0.18).roundToInt()
                    bottomSheetBehavior.peekHeight = peekHeight
                    val maxHeight = (screenHeight * 0.65).roundToInt()
                    bottomSheetBehavior.maxHeight = maxHeight
                }
            }
            isLoading.collectIn(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
            getUserData().collectIn(viewLifecycleOwner) {
                Glide.with(requireContext()).load(it?.profilePicture).centerCrop()
                    .placeholder(R.drawable.ic_profile_img_placeholder)
                    .error(R.drawable.ic_profile_img_placeholder).into(binding.ivUserProfileImage)
            }
            placeUiState.observe(viewLifecycleOwner) {
                binding.rvPlaceCards.isVisible = it == HomeViewModel.PlaceUiState.HORIZONTAL
                if (it == HomeViewModel.PlaceUiState.VERTICAL) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }
            nearByPlacesInGroup.observe(viewLifecycleOwner) {
                placesAdapter.setNearByPlaces(it)
            }
            dataState.observe(viewLifecycleOwner){
                binding.bottomsheetPlaceList.apply {
                    rvPlaces.isVisible = it != HomeViewModel.DataState.EMPTY_DATA
                    animationView.isVisible = it == HomeViewModel.DataState.EMPTY_DATA
                    tvNoPlacesFound.isVisible = it == HomeViewModel.DataState.EMPTY_DATA
                }
            }
            scrollHorizontalPlaceListToPosition.collectIn(viewLifecycleOwner) {
                binding.rvPlaceCards.scrollToPosition(it)
            }
            selectedMarker.collectIn(viewLifecycleOwner) { markerData ->
                val marker = markerData.marker
                try {
                    val resourceId = iconResourceMapping[markerData.placeUiModel.icon] ?: return@collectIn
                    val newIcon =
                        ContextCompat.getDrawable(requireContext(), resourceId)!!
                            .toBitmap(150,150)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(newIcon))
                }catch (e : Exception){
                    Log.i("JAPAN", "observeData: ${e.message}")
                }
                marker.showInfoWindow()
                moveCamera(marker.position, true)
            }
            nearByPlacesMarkerPoints.observe(viewLifecycleOwner) {
                googleMap?.clear()
                it.forEach { place ->
                    setLocationMarkers(place)
                }
            }
            nearByPlaces.observe(viewLifecycleOwner) {
                placesHorizontalAdapter.setNearByPlaces(it)
            }
            moveToLocation.observe(viewLifecycleOwner) { latLngAndShouldAnimateCamera ->
                viewModel.showVerticalUi()
                lifecycleScope.launch(Dispatchers.IO) {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        moveCamera(
                            latLngAndShouldAnimateCamera.first,
                            latLngAndShouldAnimateCamera.second
                        )
                    }
                }
            }
            searchedFormattedAddress.observe(viewLifecycleOwner) {
                binding.tvLastSearchLocation.isSelected = true
                binding.tvLastSearchLocation.text = it
            }
        }
        sharedViewModel.apply {
            onPreferencesSaved.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.resetSearchWithNewInterestes()
                    sharedViewModel.onPreferenceRead()
                }
            }
            onLocationPermissionClicked.collectIn(viewLifecycleOwner) {
                childFragmentManager.popBackStack()
                checkLocationPermission(true)
            }
        }
        MyApp.isHyperLocalServiceIsRunning.observe(viewLifecycleOwner) {
            viewModel.toggleHyperLocalPlaceSearchOnPlaceFilterMenu(it)
        }
        placesHorizontalAdapter.setOnLandMarkClicked {
            Bundle().apply {
                putString("PLACE_ID", it.placeId)
                openNavFragment(
                    LocationDetailsFragment()
                )
            }
        }
        placesAdapter.setOnLandMarkClicked {
            if(viewModel.isPlaceGeneratedOptionClicked) return@setOnLandMarkClicked Unit
            Bundle().apply {
                putString("PLACE_ID", it.placeId)
                openNavFragment(
                    LocationDetailsFragment()
                )
            }
        }
        placesAdapter.setOnLandMarkCheckBoxClicked { placeUiModel, isChecked ->
            viewModel.onPlaceSelectedForItinerary(placeUiModel.placeId, isChecked)
        }
    }

    private fun moveCamera(latLng: LatLng, shouldAnimateTheCamera: Boolean) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }
        googleMap?.setPadding(
            0,
            0,
            0,
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) 0 else (requireContext().getScreenHeight() * 0.50).toInt()
        )
        if (shouldAnimateTheCamera) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )
        } else {
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )
        }
    }


    private fun fetchPlacesNearMyLocation(latLng: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(latLng))
        viewModel.lastSearchLocationLatLng = latLng
        viewModel.moveToTheLatLng(latLng)
        viewModel.resetData()
        viewModel.fetchPlacesDetailsNearMe(latLng.latitude,latLng.longitude)
        viewModel.moveToTheLatLng(latLng)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.clear()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
        }

        googleMap?.uiSettings?.setAllGesturesEnabled(true)
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        try {
            googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json
                )
            )

        } catch (e: NotFoundException) {
            Log.e("JAPAN", "Can't find style. Error: ", e)
        }

        googleMap?.setOnMarkerClickListener(this)
        // Set the info window close listener
        googleMap?.setOnInfoWindowCloseListener(this)
        googleMap?.setOnMapClickListener { latLng ->
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )
            viewModel.showVerticalUi()
        }

        googleMap?.setOnCameraIdleListener {
            val cameraPosition = googleMap?.cameraPosition?.target ?: return@setOnCameraIdleListener

            val isAtHomePlace = isDistanceUnder150Meters(
                MyApp.userCurrentLatLng?.latitude ?: 0.0,
                MyApp.userCurrentLatLng?.longitude ?: 0.0,
                cameraPosition.latitude,
                cameraPosition.longitude
            )

            val isAtLastSearchedPlace = isDistanceUnder150Meters(
                viewModel.lastSearchLocationLatLng?.latitude ?: 0.0,
                viewModel.lastSearchLocationLatLng?.longitude ?: 0.0,
                cameraPosition.latitude,
                cameraPosition.longitude
            )

            binding.apply {
                llSearchHere.isVisible = !isAtHomePlace && !isAtLastSearchedPlace
                bottomsheetPlaceList.apply {
                    llLocateMe.root.isVisibleAndEnable(MyApp.userCurrentLatLng == null || !isAtHomePlace )
                }
                llSearchHere.setOnClickListener {
                    llSearchHere.isVisible = false
                    fetchPlacesNearMyLocation(cameraPosition)
                }
            }

        }


    }


    private fun isDistanceUnder150Meters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Boolean {
        val distance = calculateDistance(lat1, lon1, lat2, lon2)
        return distance < 150
    }


    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if(MyApp.isNewInterestsSet){
            viewModel.resetSearchWithNewInterestes()
            MyApp.isNewInterestsSet = false
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val savedBundle = Bundle()
       try{
           binding.mapView.onSaveInstanceState(savedBundle)
           outState.putBundle(MAP_VIEW_BUNDLE_KEY, savedBundle)
       }catch (e : Exception){

       }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onMarkerClick(currentMarker: Marker): Boolean {
        viewModel.showHorizontalUi()
        viewModel.onMarkerClicked(currentMarker.id)
        return false
    }


    override fun onInfoWindowClose(marker: Marker) {

        val markerData = viewModel.markerDataList.find { it.marker.id == marker.id } ?: return
        val resourceId = iconResourceMapping[markerData.placeUiModel.icon] ?: return
        val newIcon =
            ContextCompat.getDrawable(requireContext(),resourceId)!!
                .toBitmap()
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(newIcon))
    }

    override fun onCameraMove() {
        // Get the new center position of the map when the camera stops moving
        val newCenter = googleMap?.cameraPosition?.target


    }


}