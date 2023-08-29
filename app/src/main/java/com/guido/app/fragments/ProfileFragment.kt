package com.guido.app.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.guido.app.BaseFragment
import com.guido.app.Constants
import com.guido.app.DefaultLocationClient
import com.guido.app.LocationClient
import com.guido.app.R
import com.guido.app.adapters.PlacesListAdapter
import com.guido.app.adapters.PlacesTypeChipAdapter
import com.guido.app.adapters.VerticalGridCustomItemDecoration
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLocationSearchBinding
import com.guido.app.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate){


    private lateinit var viewModel : ProfileViewModel
    private lateinit var placesTypeChipAdapter : PlacesTypeChipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        placesTypeChipAdapter = PlacesTypeChipAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.apply {
                profileImage.setOnClickListener {
                    findNavController().popBackStack()
                }
                rvInterests.apply {
                    addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                    adapter = placesTypeChipAdapter
                    layoutManager = GridLayoutManager(requireContext(),5,GridLayoutManager.VERTICAL,false)
                }
            }
        placesTypeChipAdapter.setPlacesType(Constants.placeTypes)
    }




}