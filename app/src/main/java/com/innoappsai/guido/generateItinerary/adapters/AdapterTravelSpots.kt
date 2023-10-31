package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.vipulasri.timelineview.TimelineView
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.databinding.LayoutTravelSpotTimelineBinding
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.updateApiKey


class AdapterTravelSpots(
    private val appContext: Context
) : RecyclerView.Adapter<AdapterTravelSpots.TimeLineViewHolder>() {

    private var _mFeedList: List<TravelPlace> = emptyList()

    fun setData(mFeedList: List<TravelPlace>) {
        _mFeedList = mFeedList
        notifyDataSetChanged()
    }

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }


        return TimeLineViewHolder(
            LayoutTravelSpotTimelineBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent,
                false
            ), viewType
        )
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = _mFeedList[position]
        holder.bind(timeLineModel)

    }

    override fun getItemCount() = _mFeedList.size

    inner class TimeLineViewHolder(
        private val binding: LayoutTravelSpotTimelineBinding,
        viewType: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(timeLinePlaceModel: TravelPlace) {
            binding.apply {
                Glide.with(appContext)
                    .load(updateApiKey(timeLinePlaceModel.placePhotos?.firstOrNull()))
                    .into(ivPlace)
                tvTimeSlot.text = timeLinePlaceModel.timeSlots
                tvPlaceName.text = timeLinePlaceModel.placeName
                placeRating.rating = 4.5f
                tvRating.text = "13,781 reviews"
                btnNavigate.setOnClickListener {
                    openDirection(
                        appContext, timeLinePlaceModel.placeName.toString(), LatLng(
                            timeLinePlaceModel.latitude ?: 0.0,
                            timeLinePlaceModel.longitude ?: 0.0
                        )
                    )
                }
            }
        }


        init {
            binding.timeline.apply {
                initLine(viewType)
            }
        }
    }

}