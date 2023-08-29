package com.guido.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.guido.app.BaseFragment
import com.guido.app.Constants.GCP_API_KEY
import com.guido.app.MyApp
import com.guido.app.R
import com.guido.app.adapters.PlacesListAdapter
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationSearchFragment : BaseFragment<FragmentLocationSearchBinding>(FragmentLocationSearchBinding::inflate),
    OnMapReadyCallback {


    private lateinit var viewModel : LocationSearchViewModel
    private lateinit var placesAdapter : PlacesListAdapter
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesListAdapter(requireContext())
        viewModel = ViewModelProvider(this)[LocationSearchViewModel::class.java]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApp.userCurrentLocation.collectIn(viewLifecycleOwner){
            googleMap.clear()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.first,it.second), 15f))
            googleMap.addMarker(MarkerOptions().position(LatLng(it.first,it.second)))
            viewModel.fetchPlacesDetailsNearMe(
                "${it.first},${it.second}",
                5000,
                "tourist_attraction",
                "landmark",
                GCP_API_KEY
            )
        }
        binding.apply {
            ivSettings.setOnClickListener {
               // (requireActivity() as MainActivity).getCurrentLocation()
                findNavController().navigate(R.id.profileFragment)
            }
            rvLocationItems.apply {
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }



        viewModel.apply {
        nearByPlaces.collectIn(viewLifecycleOwner){
                Log.i("JAPAN", "places: $it")
                placesAdapter.setNearByPlaces(it)
            }
        }
        placesAdapter.setOnLandMarkClicked{
            Bundle().apply {
                putParcelable("LANDMARK_DATA",it)
                findNavController().navigate(R.id.locationDetailsFragment,this)
            }
        }
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment


        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLon = place.latLng ?: return
                googleMap.clear()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLon, 15f))
                googleMap.addMarker(MarkerOptions().position(latLon))
                viewModel.fetchPlacesDetailsNearMe(
                    "${latLon.latitude},${latLon.longitude}",
                    5000,
                    "tourist_attraction",
                    "landmark",
                    GCP_API_KEY
                )
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("JAPAN", "An error occurred: $status")
            }
        })

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng( 22.6920897 ,88.4474228), 15f))
        googleMap.addMarker(MarkerOptions().position(LatLng( 22.6920897 ,88.4474228)))
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


}