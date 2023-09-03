package com.guido.app.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
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
import com.guido.app.adapters.PlacesListAdapter
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import com.guido.app.model.MyClusterItem
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import com.bumptech.glide.request.transition.Transition


@AndroidEntryPoint
class LocationSearchFragment : BaseFragment<FragmentLocationSearchBinding>(FragmentLocationSearchBinding::inflate),
    OnMapReadyCallback {


    private lateinit var clusterManager: ClusterManager<MyClusterItem>
    private lateinit var viewModel : LocationSearchViewModel
    private lateinit var placesAdapter : PlacesListAdapter
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesListAdapter(requireContext())
        viewModel = ViewModelProvider(this)[LocationSearchViewModel::class.java]
    }


    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            // Get the current location
            (requireActivity() as MainActivity).getCurrentLocation()
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
        binding.apply {
            ivSettings.setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
            rvLocationItems.apply {
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        fetchPlacesNearMyLocation()
        viewModel.apply {
        nearByPlaces.collectIn(viewLifecycleOwner){
            it.forEach {placeUiModel->
                setLocationMarkers(placeUiModel.icon,placeUiModel.latLng,placeUiModel.name)
            }
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

        checkLocationPermission()
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLon = place.latLng ?: return
                googleMap.clear()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLon, 12f))
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

    private fun setLocationMarkers(markerUrl : String?,markerLatLng : LatLng?,landMarkName : String?) {
        if(markerUrl == null || markerLatLng == null || landMarkName == null)  return
        GlobalScope.launch(Dispatchers.IO) {
            val iconBitmap = getBitmapFromURL(markerUrl) ?: return@launch
            withContext(Dispatchers.Main){
                googleMap.addMarker(
                    MarkerOptions()
                        .position(markerLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))
                        .title(landMarkName)
                )
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

    fun calculateZoomLevel(radiusInKm: Double): Float {
        val zoomScale = 156543.03392 // Earth's radius times 2 * pi * inches per meter (Google's constant)
        val zoom = (16 - Math.log(radiusInKm * 1000 / zoomScale) / Math.log(2.0)).toFloat()
        return if (zoom < 1) 1f else zoom
    }

    private fun fetchPlacesNearMyLocation() {
        MyApp.userCurrentLocation.collectIn(viewLifecycleOwner){
            googleMap.clear()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.first,it.second), 12f))
            googleMap.addMarker(MarkerOptions().position(LatLng(it.first,it.second)))
            viewModel.fetchPlacesDetailsNearMe(
                "${it.first},${it.second}",
                5000,
                "tourist_attraction",
                "",
                GCP_API_KEY
            )
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.clear()
        clusterManager = ClusterManager<MyClusterItem>(requireContext(), googleMap)
//        clusterManager.renderer = RenderClusterInfoWindow(requireContext(),googleMap,clusterManager)
//        googleMap.setOnCameraIdleListener(clusterManager)
//        googleMap.setOnMarkerClickListener(clusterManager)



    }

    private fun setLocationClusters(placeUiModels: List<PlaceUiModel>) {
        val clusterItems = placeUiModels.map { MyClusterItem(it.latLng?.latitude!!,it.latLng?.longitude!!,it.name.toString(),it.address.toString(),it.icon.toString()) }
        clusterManager.addItems(clusterItems)
        clusterManager.cluster()
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


    private class RenderClusterInfoWindow constructor(
        private val context: Context,
        private val map: GoogleMap?,
        private val clusterManager: ClusterManager<MyClusterItem>
    ) :
        DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

        override fun onClusterRendered(cluster: Cluster<MyClusterItem>, marker: Marker) {
            super.onClusterRendered(cluster, marker)

        }

        override fun onBeforeClusterItemRendered(item: MyClusterItem, markerOptions: MarkerOptions) {
            markerOptions.title(item.name)
            Glide.with(context)
                .asBitmap()
                .load(item.iconUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        clusterManager.cluster()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle clearing the load state if needed
                    }
                })
            super.onBeforeClusterItemRendered(item, markerOptions)
        }
    }


}