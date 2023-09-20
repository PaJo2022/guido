package com.guido.app.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.guido.app.BaseFragment
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentLocationSearchBinding>(FragmentLocationSearchBinding::inflate),
    OnMapReadyCallback, OnMarkerClickListener , GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnCameraMoveListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter
    private lateinit var placesHorizontalAdapter: PlacesHorizontalListAdapter
    private lateinit var mapView: MapView



    // private val zoom = 16f

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        placesHorizontalAdapter = PlacesHorizontalListAdapter(requireContext())
        checkLocationPermission()
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




        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val snapHelper1: SnapHelper = PagerSnapHelper()

        binding.apply {
            bottomsheetPlaceList.rvPlaces.apply {
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            }
            binding.bottomsheetPlaceList.llLocateMe.root.setOnClickListener {
                checkLocationPermission(shouldAnimate = true)
            }
            tvSearchLocations.setOnClickListener {
                findNavController().navigate(R.id.discover_fragment)
            }

            rvPlaceCards.apply {
                adapter = placesHorizontalAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            ivSoundTimer.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
            bottomsheetPlaceList.bottomSheet
            snapHelper1.attachToRecyclerView(binding.rvPlaceCards)
        }


// Configure BottomSheet behavior

// Configure BottomSheet behavior
        bottomSheetBehavior =
            BottomSheetBehavior.from<LinearLayout>(binding.bottomsheetPlaceList.bottomSheet)


        val screenHeight = requireContext().getScreenHeight()
        val peekHeight1 = (screenHeight * 0.65).roundToInt()
        val peekHeight2 = (screenHeight * 0.30).roundToInt()

        bottomSheetBehavior.peekHeight = peekHeight1

        bottomSheetBehavior.isHideable = true




        observeData()


    }

    fun changeOffsetCenter(latitude: Double, longitude: Double) {
        val mappoint =
            googleMap?.projection?.toScreenLocation(LatLng(latitude, longitude))
        mappoint?.set(
            mappoint.x,
            mappoint.y + 1
        ) // change these values as you need , just hard coded a value if you want you can give it based on a ratio like using DisplayMetrics  as well
        if (mappoint != null) {
            googleMap?.projection?.fromScreenLocation(mappoint)?.let {
                CameraUpdateFactory.newLatLngZoom(
                    it, 12f
                )
            }?.let {
                googleMap?.animateCamera(
                    it
                )
            }
        }
    }

    private fun observeData() {
        viewModel.apply {
            placeUiState.observe(viewLifecycleOwner) {
                binding.rvPlaceCards.isVisible = it == HomeViewModel.PlaceUiState.HORIZONTAL
                binding.bottomsheetPlaceList.root.isVisible =
                    it == HomeViewModel.PlaceUiState.VERTICAL
            }
            nearByPlacesInGroup.observe(viewLifecycleOwner) {
                placesAdapter.setNearByPlaces(it)
            }
            nearByPlacesMarkerPoints.collectIn(viewLifecycleOwner) {
                Log.i("JAPAN", "observeData: ${it.size}")
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                delay(500)
                    withContext(Dispatchers.Main){
                        googleMap?.clear()
                        it.forEach { latLng ->
                            latLng?.let { it1 ->
                                addMarker(it1, "place.name".toString())
                            }
                        }
                    }
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
        googleMap?.clear()
        googleMap?.setPadding(
            0,
            0,
            0,
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) 0 else (requireContext().getScreenHeight() * 0.50).toInt()
        )
        if (shouldAnimateTheCamera) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    12f
                )
            )
        } else {
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    12f
                )
            )
        }
    }


    private fun fetchPlacesNearMyLocation(latLng: LatLng) {
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(latLng))
        viewModel.lastSearchLocationLatLng = latLng
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
                        llLocateMe.root.isVisibleAndEnable(!isAtHomePlace)
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
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onMarkerClick(currentMarker: Marker): Boolean {
        viewModel.showHorizontalUi()
        return false
    }


    private fun addMarker(latLng: LatLng, name: String) {

        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(name)
        val marker = googleMap?.addMarker(markerOptions)
        Log.i("JAPAN", "observeData: ${marker}")
        // Customize marker icon or other properties as needed.
    }

    override fun onInfoWindowClose(p0: Marker) {
        viewModel.showVerticalUi()
    }

    override fun onCameraMove() {
        // Get the new center position of the map when the camera stops moving
        val newCenter = googleMap?.cameraPosition?.target

        binding.tvSearchLocations.text = newCenter.toString()
    }


}