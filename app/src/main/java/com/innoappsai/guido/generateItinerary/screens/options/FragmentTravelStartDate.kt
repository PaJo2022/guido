package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDateBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


@AndroidEntryPoint
class FragmentTravelStartDate : BaseFragment<LayoutItinearyGenerationTravelDateBinding>(LayoutItinearyGenerationTravelDateBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Get the current date
        val currentDate = Date(System.currentTimeMillis())


        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, 1) // Add 1 year to the current date

        // Set the minimum date to the current date
        binding.calender.minDate = currentDate.time
        // Set the maximum date to 1 year in the future
        binding.calender.maxDate = maxDate.timeInMillis
        // Optionally, you can set an event listener to handle date selection
        binding.calender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Date(year - 1900, month, dayOfMonth)
            // Handle the selected date
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val formattedDate = sdf.format(selectedDate)
            // Do something with the selected date
        }
    }
}