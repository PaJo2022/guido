package com.innoappsai.guido.generateItinerary.screens

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.Constants
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.fragments.LocationDetailsFragment
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelDate
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpotViewPager
import com.innoappsai.guido.generateItinerary.model.TripDataForNotification
import com.innoappsai.guido.generateItinerary.receiver.TripNotificationBroadCastReceiver
import com.innoappsai.guido.getScreenHeight
import com.innoappsai.guido.model.MarkerData
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt

@AndroidEntryPoint
class FragmentPlaceItinerary :
    BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate),
    OnMapReadyCallback {


    private val zoom = 13f
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val viewModel: ViewModelFragmentPlaceItinerary by viewModels()
    private lateinit var mAdapter: AdapterTravelDate
    private lateinit var adapterTravelSpotViewPager: AdapterTravelSpotViewPager
    private var googleMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = AdapterTravelDate(requireContext())
        adapterTravelSpotViewPager = AdapterTravelSpotViewPager(requireContext())
    }

    private fun initRecyclerView() {
        binding.bottomsheetPlaceItineary.apply {
            rvTravelDates.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                adapter = mAdapter
            }
            rvTravelTimeline.apply {
                isUserInputEnabled = false
                adapter = adapterTravelSpotViewPager
            }
        }
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
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    private fun setBottomSheetBehavior() {
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomsheetPlaceItineary.mainLayout)

        val screenHeight = requireContext().getScreenHeight()
        val peekHeight1 = (screenHeight * 0.18).roundToInt()
        bottomSheetBehavior.peekHeight = peekHeight1
        val margin = (screenHeight * 0.10).roundToInt()
        val maxHeight = (screenHeight * 0.80).roundToInt()
        bottomSheetBehavior.maxHeight = maxHeight
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet is fully expanded
                        googleMap?.setPadding(
                            0,
                            0,
                            0,
                            (requireContext().getScreenHeight() * 0.60).toInt()
                        )
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                MyApp.userCurrentLatLng!!, zoom
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
                                MyApp.userCurrentLatLng!!, zoom
                            )
                        )

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
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) 0 else (requireContext().getScreenHeight() * 0.60).toInt()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        setBottomSheetBehavior()
        viewModel.apply {
            itineraryBasicDetails.observe(viewLifecycleOwner) { travelData ->
                binding.bottomsheetPlaceItineary.apply {
                    tvPlaceName.text = travelData.placeName
                    tvPlaceName.isSelected = true
                    tvPlaceCountry.text = travelData.countryName
                    tvTripExtraDetails.text =
                        "${travelData.tripLength}, ${travelData.tripPartners}"
                }
            }

            generatedItinerary.observe(viewLifecycleOwner) { tripData ->
                try {

                    mAdapter.setData(tripData)
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            generatedItineraryWithTravelPlaceAndDirection.observe(viewLifecycleOwner) {
                try {
                    adapterTravelSpotViewPager.setData(it)
                } catch (e: Exception) {
                    Log.i("JAPAN", "Error: ${e}")
                }
            }
            tripPlaceNotificationList.observe(viewLifecycleOwner) {
                it.forEach { item ->
                    setTripNotifications(item)
                }
            }
            moveToDayIndex.observe(viewLifecycleOwner) {
                binding.bottomsheetPlaceItineary.rvTravelTimeline.setCurrentItem(it, true)
            }
            tripLocation.observe(viewLifecycleOwner) {
                googleMap?.clear()
                it.forEach { place ->
                    if (place != null) {
                        setLocationMarkers(place)
                    }
                }
            }
            moveMapTo.observe(viewLifecycleOwner) {
                if (it != null) {
                    moveCamera(it, false)
                }
            }
        }
        initRecyclerView()

        adapterTravelSpotViewPager.setOnTravelDateClickListener{
            navigateToPlaceDetailsScreen(it)
        }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

        mAdapter.setOnTravelDateClickListener {
            viewModel.onDaySelected(it)
        }

    }

    private fun setLocationMarkers(placeUiModel: PlaceUiModel) {
        val markerUrl = placeUiModel.icon
        val markerLatLng = placeUiModel.latLng
        val landMarkName = placeUiModel.name
        val resourceId = Constants.iconResourceMapping[placeUiModel.icon]
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

    private fun setTripNotifications(data: TripDataForNotification) {
        val alarmMgr = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(requireContext(), TripNotificationBroadCastReceiver::class.java)
        alarmIntent.putExtra("TRIP_PLACE_NAME", data.placeName)
        alarmIntent.putExtra("TRIP_PLACE_IMAGE", data.placeImage)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), abs(data.placeId.hashCode()), alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse(data.notificationDateAndTime)
        getTimeDifferenceInMinutes(data.notificationDateAndTime)




        if (date == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Check if the device can schedule exact alarms on Android 12+
            if (alarmMgr.canScheduleExactAlarms()) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
            } else {
                // Handle the case where exact alarms can't be scheduled
                // You may consider using inexact alarms in this case
                // Or adapt your app's functionality accordingly
                alarmMgr.set(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
            }
        } else {
            // For devices prior to Android 12, use setExact directly
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
        }
        Log.i("JAPAN", "ALARM SET")
    }

    private fun getTimeDifferenceInMinutes(formattedTime: String) {
        val currentTime = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(currentTime)

        // Parse the formattedTime and current time
        val formattedDateTime = sdf.parse(formattedTime)
        val currentDateTime = sdf.parse(formattedDate)

        // Calculate the time difference in milliseconds
        val timeDifferenceMillis = formattedDateTime.time - currentDateTime.time

        // Convert the time difference to minutes
        val timeDifferenceMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)

        Log.i("JAPAN", "getTimeDifferenceInMinutes: ${timeDifferenceMinutes}")
    }


    private fun navigateToPlaceDetailsScreen(placeId: String) {
        Bundle().apply {
            putString("PLACE_ID", placeId)
            openNavFragment(
                LocationDetailsFragment(), this
            )
        }
    }

    private fun openNavFragment(
        f: Fragment,
        args: Bundle? = null
    ) {
        FragmentUtils.replaceFragment(
            (activity as MainActivity),
            binding.mainLayout.id,
            f,
            args,
            true
        )
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
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
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                MyApp.userCurrentLatLng!!, zoom
            )
        )
        viewModel.generatePlaceItineraryById()
    }
}