package com.innoappsai.guido.generateItinerary.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.FragmentUtils
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.fragments.LocationDetailsFragment
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelDate
import com.innoappsai.guido.generateItinerary.adapters.AdapterTravelSpotViewPager
import com.innoappsai.guido.generateItinerary.receiver.TripNotificationBroadCastReceiver
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class FragmentPlaceItinerary : BaseFragment<FragmentPlaceItinearyBinding>(FragmentPlaceItinearyBinding::inflate) {


    private val viewModel: ViewModelFragmentPlaceItinerary by viewModels()
    private lateinit var mAdapter: AdapterTravelDate
    private lateinit var adapterTravelSpotViewPager: AdapterTravelSpotViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //saveItineraryId = arguments?.getString("ITINERARY_DB_ID", "") ?: ""
        mAdapter = AdapterTravelDate(requireContext())
        adapterTravelSpotViewPager = AdapterTravelSpotViewPager(requireContext())
    }

    private fun initRecyclerView() {
        val snapHelper = PagerSnapHelper()
        binding.apply {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.apply {
            generatePlaceItineraryById()
            itineraryBasicDetails.observe(viewLifecycleOwner) { travelData ->
                binding.apply {
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
                it.forEachIndexed {index,item->
                    setTripNotifications(index,item)
                }
            }
            moveToDayIndex.observe(viewLifecycleOwner) {
                binding.rvTravelTimeline.setCurrentItem(it, true)
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

    private fun setTripNotifications(id:Int,data: Triple<String, String, String>) {
        val alarmMgr = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(requireContext(), TripNotificationBroadCastReceiver::class.java)
        alarmIntent.putExtra("TRIP_PLACE_NAME", data.second)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), id, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        Log.i("JAPAN", "alarmIntent: ${alarmIntent}")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = sdf.parse(data.third)
        getTimeDifferenceInMinutes(data.third)

//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, hour)  // Replace selectedHour with your desired hour
//            set(Calendar.MINUTE, minute)     // Replace selectedMinute with your desired minute
//            set(Calendar.SECOND, 0)
//            if (timeInMillis < System.currentTimeMillis()) {
//                add(Calendar.DAY_OF_YEAR, 1)
//            }
//        }
        if (date == null) {
            Log.i("JAPAN", "NOT ALARM SET")
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
            }
        } else {
            // For devices prior to Android 12, use setExact directly
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
        }
        Log.i("JAPAN", "ALARM SET")
    }

    fun getTimeDifferenceInMinutes(formattedTime: String) {
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
        FragmentUtils.replaceFragment((activity as MainActivity), binding.mainLayout.id, f,args,true)
    }
}