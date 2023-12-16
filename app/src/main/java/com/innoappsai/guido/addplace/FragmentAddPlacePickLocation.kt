package com.innoappsai.guido.addplace

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlacesAutoCompleteAdapter
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlacePickLocationBinding
import com.innoappsai.guido.fragments.SearchLocationViewModel
import com.innoappsai.guido.toggleEnableAndAlpha
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlacePickLocation :
    BaseFragment<FragmentAddPlacePickLocationBinding>(FragmentAddPlacePickLocationBinding::inflate),
    OnMapReadyCallback, GoogleMap.OnCameraMoveListener {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private val searchLocationViewModel: SearchLocationViewModel by viewModels()
    private val zoom = 13f
    private var googleMap: GoogleMap? = null
    private lateinit var adapterPlaceAutoComplete: PlacesAutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.apply {
            ivArrowBack.setOnClickListener { requireActivity().finish() }
            etSearchLocation.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrEmpty()) {
                    searchLocationViewModel.getPredictions(text.toString())
                }
            }
            rvPlaceSuggestions.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            tvNext.setOnClickListener {
                binding.etSearchLocation.text = null
                binding.rvPlaceSuggestions.isVisible = false
                findNavController().navigate(R.id.fragmentChoosePlaceType)
            }
        }
        checkLocationPermission(shouldAnimate = true)
        viewModel.apply {
            moveToLocation.observe(viewLifecycleOwner) {
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it.first,
                        zoom
                    )
                )
            }
            searchedFormattedAddress.observe(viewLifecycleOwner) {
                binding.tvLocationFullAddress.text = it ?: "Searching..."
            }
            isLoading.collectIn(viewLifecycleOwner) {
                (requireActivity() as AddPlaceActivity).toggleLoading(it)
                binding.tvNext.toggleEnableAndAlpha(!it)
            }
        }
        searchLocationViewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                binding.rvPlaceSuggestions.isVisible = it.isNotEmpty()
                adapterPlaceAutoComplete.setPredications(it)
            }
            navigateBack.collectIn(viewLifecycleOwner){
                binding.rvPlaceSuggestions.isVisible = false
                binding.etSearchLocation.setText("")
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(it.latitude, it.longitude),
                        zoom
                    )
                )
            }
        }
        adapterPlaceAutoComplete.setOnPlaceSelected {
            searchLocationViewModel.getSelectedPlaceLatLon(it)
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
                    Bundle().apply {
                        putBoolean("SHOULD_GO_TO_SETTINGS", true)
                        findNavController().navigate(R.id.bottomSheetAskLocationPermission, this)
                    }
                } else {
                    // Permission is denied (user selected "Deny" but not "Never ask again")
                    // You can handle this case by showing a rationale
                    Bundle().apply {
                        putBoolean("SHOULD_GO_TO_SETTINGS", false)
                        findNavController().navigate(R.id.bottomSheetAskLocationPermission, this)
                    }
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

        } catch (e: Resources.NotFoundException) {
            Log.e("JAPAN", "Can't find style. Error: ", e)
        }



        googleMap?.setOnMapClickListener { latLng ->
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )

        }

        googleMap?.setOnCameraMoveListener {
            viewModel.onGoogleMapMoving()
        }

        googleMap?.setOnCameraIdleListener {
            val cameraPosition = googleMap?.cameraPosition?.target ?: return@setOnCameraIdleListener

            viewModel.fetchCurrentAddressFromGeoCoding(
                cameraPosition.latitude,
                cameraPosition.longitude
            )

        }

    }

    override fun onCameraMove() {

    }


    override fun onResume() {
        super.onResume()

        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}