package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.innoappsai.guido.R
import com.innoappsai.guido.TraveingType
import com.innoappsai.guido.calculateTravelTimeAndMode
import com.innoappsai.guido.databinding.LayoutTravelSpotDirectionTimelineBinding
import com.innoappsai.guido.databinding.LayoutTravelSpotTimelineBinding
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.showDirectionsOnGoogleMaps
import com.innoappsai.guido.updateApiKey


class AdapterTravelSpots(
    private val appContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private enum class UiType {
        TRAVEL_PLACE, TRAVEL_DIRECTION
    }

    private var _mFeedList: List<TravelPlaceWithTravelDirection> = emptyList()

    fun setData(mFeedList: List<TravelPlaceWithTravelDirection>) {
        _mFeedList = mFeedList
        notifyDataSetChanged()
    }

    private var _onLandMarkSelectedListener: ((id : String) -> Any?)? =
        null

    fun setOnTravelDateClickListener(onLandMarkSelectedListener: ((id : String) -> Any?)) {
        _onLandMarkSelectedListener = onLandMarkSelectedListener
    }



    override fun getItemViewType(position: Int): Int {
        return if (_mFeedList[position].travelPlace == null) UiType.TRAVEL_DIRECTION.ordinal else UiType.TRAVEL_PLACE.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            UiType.TRAVEL_PLACE.ordinal -> {
                TimeLineViewHolder(
                    LayoutTravelSpotTimelineBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent,
                        false
                    )
                )
            }

            else -> {
                TimeLineTravelDirectionViewHolder(
                    LayoutTravelSpotDirectionTimelineBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val timeLineModel = _mFeedList[position]
        if (holder is TimeLineViewHolder) {
            timeLineModel.travelPlace?.let { holder.bind(position,it) }
        } else if (holder is TimeLineTravelDirectionViewHolder) {
            timeLineModel.travelDirection?.let {
                holder.bind(it)
            }
        }

    }

    override fun getItemCount() = _mFeedList.size

    inner class TimeLineViewHolder(
        private val binding: LayoutTravelSpotTimelineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, travelPlace: TravelPlace) {
            val timeLineType = if(position == 0) 1 else if(position == _mFeedList.size - 1) 2 else 0
            binding.apply {
                root.setOnClickListener {
                    _onLandMarkSelectedListener?.invoke(travelPlace.placeId)
                }
                timeline.initLine(timeLineType)
                Glide.with(appContext)
                    .load(updateApiKey(travelPlace.placePhoto))
                    .centerCrop()
                    .into(ivPlace)
                tvTimeSlot.text = travelPlace.timeSlots
                tvPlaceName.text = travelPlace.placeName
                placeRating.rating = 4.5f
                tvRating.text = "13,781 reviews"
                btnNavigate.setOnClickListener {
                    openDirection(
                        appContext, travelPlace.placeName, LatLng(
                            travelPlace.latitude,
                            travelPlace.longitude
                        )
                    )
                }
            }
        }


    }

    inner class TimeLineTravelDirectionViewHolder(
        private val binding: LayoutTravelSpotDirectionTimelineBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(travelDirection: TravelDirection) {
            val travelTimeAndType = calculateTravelTimeAndMode(
                travelDirection.currentLocation?.latitude ?: 0.0,
                travelDirection.currentLocation?.longitude ?: 0.0,
                travelDirection.nextLocation?.latitude ?: 0.0,
                travelDirection.nextLocation?.longitude ?: 0.0,
            )
            binding.apply {
                binding.tvTravelTime.text = travelTimeAndType.second
                binding.ivTravel.setImageResource(if(travelTimeAndType.first == TraveingType.WALKING)  R.drawable.ic_walking else R.drawable.ic_car)
                binding.tvTravelDirection.setOnClickListener {
                    showDirectionsOnGoogleMaps(
                        appContext,
                        sourceLat = travelDirection.currentLocation?.latitude ?: 0.0,
                        sourceLon = travelDirection.currentLocation?.longitude ?: 0.0,
                        destinationLat = travelDirection.nextLocation?.latitude ?: 0.0,
                        destinationLon = travelDirection.nextLocation?.longitude ?: 0.0,
                        sourceName = travelDirection.currentLocationName.toString(),
                        destinationName = travelDirection.nextLocationName.toString()
                    )
                }
                timeline.initLine(0)
            }

        }


    }

}