package com.guido.app.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.guido.app.Constants.GCP_API_KEY
import com.guido.app.MainActivity.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.guido.app.MyApp
import com.guido.app.MyApp.Companion.googleMap
import com.guido.app.R
import com.guido.app.adapters.PlacesGroupListAdapter
import com.guido.app.adapters.PlacesHorizontalListAdapter
import com.guido.app.calculateDistance
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import com.guido.app.db.AppPrefs
import com.guido.app.getScreenHeight
import com.guido.app.isVisibleAndEnable
import com.guido.app.model.MarkerData
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment(),
    OnMapReadyCallback, OnMarkerClickListener , GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnCameraMoveListener {

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "mapview_bundle_key"
    }

    private var _binding: FragmentLocationSearchBinding? = null
    private val binding: FragmentLocationSearchBinding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter
    private lateinit var placesHorizontalAdapter: PlacesHorizontalListAdapter




    // private val zoom = 16f

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        placesHorizontalAdapter = PlacesHorizontalListAdapter(requireContext())
        checkLocationPermission()
    }


    private var hasMapConfigured: Boolean = false

    private val viewPersistedMapBundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationSearchBinding.inflate(layoutInflater, container, false)
        val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        initMapViewState(binding.mapView, mapViewBundle)
        binding.mapView.getMapAsync(this)
        return binding.root
    }


    private fun initMapViewState(mapView: MapView, savedMapViewBundle: Bundle?) {
        // The state persisted across Fragment transaction.
        if (!viewPersistedMapBundle.isEmpty) {
            mapView.onCreate(viewPersistedMapBundle)
            hasMapConfigured = true
            return
        }
        // The state persisted across Fragment recreation.
        mapView.onCreate(savedMapViewBundle)
        hasMapConfigured = savedMapViewBundle != null
    }

    private fun checkLocationPermission(shouldAnimate: Boolean = false) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            // Get the current location
            viewModel.fetchCurrentLocation(shouldAnimate)
        } else {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val snapHelper1: SnapHelper = PagerSnapHelper()
        val placeCardHorizontalLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.apply {
            bottomsheetPlaceList.rvPlaces.apply {
                adapter = placesAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            binding.bottomsheetPlaceList.llLocateMe.root.setOnClickListener {
                checkLocationPermission(shouldAnimate = true)
            }
            tvSearchLocation.setOnClickListener {
                findNavController().navigate(R.id.discover_fragment)
            }

            rvPlaceCards.apply {
                adapter = placesHorizontalAdapter
                layoutManager = placeCardHorizontalLayoutManager
            }
            ivSoundTimer.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
            bottomsheetPlaceList.bottomSheet
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
        val peekHeight1 = (screenHeight * 0.15).roundToInt()
        val margin = (screenHeight * 0.10).roundToInt()
        val maxHeight = (screenHeight * 0.65).roundToInt()

        bottomSheetBehavior.peekHeight = peekHeight1
        bottomSheetBehavior.maxHeight = maxHeight
        bottomSheetBehavior.maxHeight = maxHeight

        bottomSheetBehavior.isHideable = false


        val layoutParams = binding.rvPlaceCards.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = margin
        binding.rvPlaceCards.layoutParams = layoutParams


        observeData()


    }

    private fun setLocationMarkers(placeUiModel: PlaceUiModel) {
        val markerUrl = placeUiModel.icon
        val markerLatLng = placeUiModel.latLng
        val landMarkName = placeUiModel.name

        if (markerUrl == null || markerLatLng == null || landMarkName == null) return
        GlobalScope.launch(Dispatchers.IO) {
            val iconBitmap = getBitmapFromURL(markerUrl)  ?: return@launch
            withContext(Dispatchers.Main) {

                val markerOptions = MarkerOptions()
                    .position(markerLatLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                    .title(landMarkName)
                val marker = googleMap?.addMarker(markerOptions)
                marker?.let {
                    viewModel.markerDataList.add(MarkerData(it, placeUiModel))
                }


            }
        }
    }

    private fun getBitmapFromURL(url: String?): Bitmap? {
        return try {
            val inputStream = URL(url).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun observeData() {
        viewModel.apply {
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
            scrollHorizontalPlaceListToPosition.collectIn(viewLifecycleOwner) {
                binding.rvPlaceCards.scrollToPosition(it)
            }
            selectedMarker.collectIn(viewLifecycleOwner) { marker ->


                // You can also show an info window
                marker.showInfoWindow()

                moveCamera(marker.position, true)
            }
            nearByPlacesMarkerPoints.collectIn(viewLifecycleOwner) {
                Log.i("JAPAN", "observeData: ${it.size}")
                googleMap?.clear()
                it.forEach { place ->
                    setLocationMarkers(place)
                }
            }
            nearByPlaces.observe(viewLifecycleOwner) {
                placesHorizontalAdapter.setNearByPlaces(it)
            }
            currentLatLng.observe(viewLifecycleOwner) { latLng ->
                val markerOptions = MarkerOptions()
                    .position(latLng)
                googleMap?.addMarker(markerOptions)
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
        sharedViewModel.onPreferencesSaved.collectIn(viewLifecycleOwner){
            viewModel.resetSearchWithNewInterestes()
        }
        placesHorizontalAdapter.setOnLandMarkClicked {
            Bundle().apply {
                putParcelable("LANDMARK_DATA", it)
                findNavController().navigate(R.id.locationDetailsFragment, this)
            }
        }
        placesAdapter.setOnLandMarkClicked {
            Bundle().apply {
                putParcelable("LANDMARK_DATA", it)
                findNavController().navigate(R.id.locationDetailsFragment, this)
            }
        }
    }

    private fun moveCamera(latLng: LatLng, shouldAnimateTheCamera: Boolean) {
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
                    15f
                )
            )
        } else {
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    15f
                )
            )
        }
    }


    private fun fetchPlacesNearMyLocation(latLng: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(latLng))
        viewModel.lastSearchLocationLatLng = latLng
        viewModel.moveToTheLatLng(latLng)
        viewModel.fetchPlacesDetailsNearMe(
            "${latLng.latitude},${latLng.longitude}",
            appPrefs.prefDistance,
            "tourist_attraction",
            "",
            GCP_API_KEY
        )
        viewModel.fetchCurrentAddressFromGeoCoding(
            "${latLng.latitude},${latLng.longitude}",
            GCP_API_KEY
        )
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), com.guido.app.R.raw.style_json
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
                    12f
                )
            )
            viewModel.showVerticalUi()
        }

        googleMap?.setOnCameraIdleListener {
            val cameraPosition = googleMap?.cameraPosition?.target ?: return@setOnCameraIdleListener
            MyApp.userCurrentLatLng?.let {
                val isAtHomePlace = isDistanceUnder150Meters(
                    MyApp.userCurrentLatLng!!.latitude,
                    MyApp.userCurrentLatLng!!.longitude,
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
                        llLocateMe.root.isVisibleAndEnable(!isAtHomePlace && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    }
                    llSearchHere.setOnClickListener {
                        llSearchHere.isVisible = false
                        fetchPlacesNearMyLocation(cameraPosition)
                    }
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
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //binding.mapView.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onSaveInstanceState(viewPersistedMapBundle)
        binding.mapView.onDestroy()
        _binding = null
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val savedBundle = Bundle()
        binding.mapView.onSaveInstanceState(savedBundle)
        outState.putBundle(MAP_VIEW_BUNDLE_KEY, savedBundle)
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


    private fun addMarker(latLng: LatLng, name: String) {

        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(name)
        val marker = googleMap?.addMarker(markerOptions)
        // Customize marker icon or other properties as needed.
    }

    override fun onInfoWindowClose(p0: Marker) {

        // viewModel.showVerticalUi()
    }

    override fun onCameraMove() {
        // Get the new center position of the map when the camera stops moving
        val newCenter = googleMap?.cameraPosition?.target


    }


}