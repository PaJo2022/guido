package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutTravelSpotViewpagerItemBinding
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData


class AdapterTravelSpotViewPager(
    private val appContext: Context
) : RecyclerView.Adapter<AdapterTravelSpotViewPager.AdapterTravelSpotViewPagerViewHolder>() {

    private var _eachDayTravelSpotItemList: List<TripData> = ArrayList()

    fun setData(eachDayTravelSpotItemList: List<TripData>) {
        _eachDayTravelSpotItemList = eachDayTravelSpotItemList
        notifyDataSetChanged()
    }

    inner class AdapterTravelSpotViewPagerViewHolder(
        private val adapterTravelSpots: AdapterTravelSpots,
        private val binding: LayoutTravelSpotViewpagerItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tripData: TripData) {
            binding.rvEachDayTravelSpot.apply {
                adapter = adapterTravelSpots
                layoutManager = LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false)
            }
            tripData.travelPlaces?.let { adapterTravelSpots.setData(it) }
            binding.apply {
                rvEachDayTravelSpot.isVisible = tripData.travelPlaces?.isNotEmpty() == true
                tvNoActivitiesAdded.isVisible = tripData.travelPlaces?.isNotEmpty() == false
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTravelSpotViewPagerViewHolder {
        val adapterTravelSpots = AdapterTravelSpots(appContext)
        return AdapterTravelSpotViewPagerViewHolder(
            adapterTravelSpots,
            LayoutTravelSpotViewpagerItemBinding.inflate(
                LayoutInflater.from(appContext), parent,
                false
            )
        )
    }

    override fun getItemCount() = _eachDayTravelSpotItemList.size

    override fun onBindViewHolder(holder: AdapterTravelSpotViewPagerViewHolder, position: Int) {
        val travelSpot = _eachDayTravelSpotItemList[position]
        holder.bind(travelSpot)
    }

}