package com.guido.app.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.guido.app.BaseFragment
import com.guido.app.Constants.GCP_API_KEY
import com.guido.app.MainActivity
import com.guido.app.MainActivity.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.guido.app.MyApp
import com.guido.app.R
import com.guido.app.adapters.PlacesGroupListAdapter
import com.guido.app.adapters.PlacesHorizontalListAdapter
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import com.guido.app.db.AppPrefs
import com.guido.app.model.MarkerData
import com.guido.app.model.MyClusterItem
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.inject.Inject


@AndroidEntryPoint
class LocationSearchFragment :
    BaseFragment<FragmentLocationSearchBinding>(FragmentLocationSearchBinding::inflate),
    OnMapReadyCallback, OnMarkerClickListener {



    private  val viewModel: LocationSearchViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter
    private lateinit var placesHorizontalAdapter: PlacesHorizontalListAdapter
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    // private val zoom = 16f

    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        placesHorizontalAdapter = PlacesHorizontalListAdapter(requireContext())
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
                checkLocationPermission()
            }
            rvPlaces.apply {
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            }
            rvPlaceCards.apply {
                adapter = placesHorizontalAdapter
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }
            snapHelper1.attachToRecyclerView(binding.rvPlaceCards)
        }


        checkLocationPermission()

        observeData()
        val bottomSheetContainer = binding.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        // Set the initial state (Collapsed, Expanded, etc.)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Handle user interactions to expand or collapse the Bottom Sheet
        bottomSheetContainer.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }


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
                placesHorizontalAdapter.setNearByPlaces(it)
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

        return true
    }



}