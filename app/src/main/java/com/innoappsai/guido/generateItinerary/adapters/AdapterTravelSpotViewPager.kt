package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutTravelSpotViewpagerItemBinding
import com.innoappsai.guido.generateItinerary.model.TripDataForNotification
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData


class AdapterTravelSpotViewPager(
    private val appContext: Context
) : RecyclerView.Adapter<AdapterTravelSpotViewPager.AdapterTravelSpotViewPagerViewHolder>() {

    private var _eachDayTravelSpotItemList: List<List<TravelPlaceWithTravelDirection>> = ArrayList()

    fun setData(eachDayTravelSpotItemList: List<List<TravelPlaceWithTravelDirection>>) {
        _eachDayTravelSpotItemList = eachDayTravelSpotItemList
        notifyDataSetChanged()
    }

    private var _onLandMarkSelectedListener: ((id : String) -> Any?)? =
        null

    fun setOnTravelDateClickListener(onLandMarkSelectedListener: ((id : String) -> Any?)) {
        _onLandMarkSelectedListener = onLandMarkSelectedListener
    }


    private var _onLandMarkNotificationToggle: ((tripDataForNotification : TripDataForNotification,isEnabled : Boolean) -> Any?)? =
        null

    fun setOnLandMarkNotificationToggle(onLandMarkNotificationToggle: ((tripDataForNotification : TripDataForNotification, isEnabled : Boolean) -> Any?)) {
        _onLandMarkNotificationToggle = onLandMarkNotificationToggle
    }

    inner class AdapterTravelSpotViewPagerViewHolder(
        private val adapterTravelSpots: AdapterTravelSpots,
        private val binding: LayoutTravelSpotViewpagerItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(travelPlaces: List<TravelPlaceWithTravelDirection>) {
            binding.rvEachDayTravelSpot.apply {
                adapter = adapterTravelSpots
                layoutManager = LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false)
            }
            adapterTravelSpots.setData(travelPlaces)
            adapterTravelSpots.setOnTravelDateClickListener {
                _onLandMarkSelectedListener?.invoke(it)
            }
            adapterTravelSpots.setOnLandMarkNotificationToggle { time, isEnabled ->
                _onLandMarkNotificationToggle?.invoke(time, isEnabled)
            }
            binding.apply {
                rvEachDayTravelSpot.isVisible = travelPlaces.isNotEmpty()
                tvNoActivitiesAdded.isVisible = travelPlaces.isNotEmpty() == false
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