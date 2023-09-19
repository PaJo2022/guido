package com.guido.app.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
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
import com.guido.app.R
import com.guido.app.adapters.PlacesGroupListAdapter
import com.guido.app.adapters.PlacesHorizontalListAdapter
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import com.guido.app.db.AppPrefs
import com.guido.app.isVisibleAndEnable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentLocationSearchBinding>(FragmentLocationSearchBinding::inflate),
    OnMapReadyCallback, OnMarkerClickListener , GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnCameraMoveListener {



    private  val viewModel: HomeViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter
    private lateinit var placesHorizontalAdapter: PlacesHorizontalListAdapter
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var mMap: GoogleMap

    // private val zoom = 16f

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        placesHorizontalAdapter = PlacesHorizontalListAdapter(requireContext())
        checkLocationPermission()
    }


    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            // Get the current location
            viewModel.fetchCurrentLocation()
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
            llSearchHere.setOnClickListener {

            }
            bottomsheetPlaceList.rvPlaces.apply {
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            }
            binding.bottomsheetPlaceList.llLocateMe.root.setOnClickListener {
                checkLocationPermission()
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
        val bottomSheetBehavior =
            BottomSheetBehavior.from<LinearLayout>(binding.bottomsheetPlaceList.bottomSheet)



        val peekHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
        bottomSheetBehavior.peekHeight = peekHeight
        bottomSheetBehavior.isHideable = false


        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Handle state changes as needed
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        observeData()


    }

    private fun observeData() {
        viewModel.apply {
            currentLatLng.collectIn(viewLifecycleOwner){
                fetchPlacesNearMyLocation(it)
            }
            nearByPlacesInGroup.collectIn(viewLifecycleOwner){
                placesAdapter.setNearByPlaces(it)
            }
            nearByPlaces.collectIn(viewLifecycleOwner){
                it.forEach {place->
                    place.latLng?.let { it1 -> addMarker(it1,place.name.toString()) }
                }
                placesHorizontalAdapter.setNearByPlaces(it)
            }
            moveToLocation.collectIn(viewLifecycleOwner){latLng->
                googleMap.clear()
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        12f
                    )
                )
            }
        }
        sharedViewModel.apply {
            onLocationSelected.observe(viewLifecycleOwner){
                viewModel.fetchPlaceDetailsById(it)
            }
        }
    }


    private fun fetchPlacesNearMyLocation(latLng: LatLng) {
        googleMap.clear()
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                12f
            )
        )
        googleMap.addMarker(MarkerOptions().position(latLng))
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
        googleMap.clear()
        googleMap.uiSettings.setAllGesturesEnabled(true)
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), com.guido.app.R.raw.style_json
                )
            )

        } catch (e: NotFoundException) {
            Log.e("JAPAN", "Can't find style. Error: ", e)
        }

        googleMap.setOnMarkerClickListener(this)
        // Set the info window close listener
        googleMap.setOnInfoWindowCloseListener(this)
        googleMap.setOnMapClickListener { latLng ->
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    12f
                )
            )
            binding.rvPlaceCards.isVisible = false
        }

        googleMap.setOnCameraIdleListener {
            val cameraPosition = googleMap.cameraPosition.target
            MyApp.userCurrentLatLng?.let {
                val isAtHomePlace = isDistanceUnder50Meters(
                    MyApp.userCurrentLatLng!!.latitude,
                    MyApp.userCurrentLatLng!!.longitude,
                    cameraPosition.latitude,
                    cameraPosition.longitude
                )

                binding.apply {
                    llSearchHere.isVisible = !isAtHomePlace
                    bottomsheetPlaceList.apply {
                        llLocateMe.root.isVisibleAndEnable(!isAtHomePlace)
                    }
                    llSearchHere.setOnClickListener {
                        fetchPlacesNearMyLocation(cameraPosition)
                    }
                }
            }

        }


    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371 // Earth's radius in kilometers

        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Calculate the differences
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad

        // Calculate the Haversine distance
        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Calculate the distance in meters
        val distance = radius * c * 1000 // Convert to meters

        return distance
    }

    fun isDistanceUnder50Meters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Boolean {
        val distance = calculateDistance(lat1, lon1, lat2, lon2)
        return distance < 50
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
        binding.rvPlaceCards.isVisible = true
        return false
    }


    private fun addMarker(latLng: LatLng, name: String) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(name)
        val marker = googleMap.addMarker(markerOptions)
        // Customize marker icon or other properties as needed.
    }

    override fun onInfoWindowClose(p0: Marker) {
       binding.rvPlaceCards.isVisible = false
    }

    override fun onCameraMove() {
        // Get the new center position of the map when the camera stops moving
        val newCenter = mMap.cameraPosition.target

        binding.tvSearchLocations.text = newCenter.toString()
    }


}