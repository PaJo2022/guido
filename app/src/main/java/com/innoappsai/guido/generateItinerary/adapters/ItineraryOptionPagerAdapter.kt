package com.innoappsai.guido.generateItinerary.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ItineraryOptionPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private var _options: List<Fragment> = emptyList()

    fun setItineraryOptions(options: List<Fragment>) {
        _options = options
    }

    override fun getItemCount() = _options.size

    override fun createFragment(position: Int) = _options[position]


}
