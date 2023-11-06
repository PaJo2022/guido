package com.innoappsai.guido.generateItinerary.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelBudgetType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelCompanionType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelDuration
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelPlaceTypes
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelStartDate

class ItineraryOptionPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private var _options: List<Fragment> = emptyList()

    fun setItineraryOptions(options: List<Fragment>) {
        _options = options
    }

    override fun getItemCount() = _options.size

    override fun createFragment(position: Int) = _options[position]


}
