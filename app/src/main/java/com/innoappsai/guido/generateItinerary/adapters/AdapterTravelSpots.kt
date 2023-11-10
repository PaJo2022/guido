package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.innoappsai.guido.R
import com.innoappsai.guido.TraveingType
import com.innoappsai.guido.adapters.ImageSliderAdapter
import com.innoappsai.guido.calculateTravelTimeAndMode
import com.innoappsai.guido.databinding.LayoutTravelSpotDirectionTimelineBinding
import com.innoappsai.guido.databinding.LayoutTravelSpotTimelineBinding
import com.innoappsai.guido.generateItinerary.model.TripDataForNotification
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelDirection
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlace
import com.innoappsai.guido.generateItinerary.model.itinerary.TravelPlaceWithTravelDirection
import com.innoappsai.guido.openDirection
import com.innoappsai.guido.showDirectionsOnGoogleMaps


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

    private var _onLandMarkSelectedListener: ((id: String) -> Any?)? =
        null

    fun setOnTravelDateClickListener(onLandMarkSelectedListener: ((id: String) -> Any?)) {
        _onLandMarkSelectedListener = onLandMarkSelectedListener
    }

    private var _onLandMarkNotificationToggle: ((tripDataForNotification: TripDataForNotification, isEnabled: Boolean) -> Any?)? =
        null

    fun setOnLandMarkNotificationToggle(onLandMarkNotificationToggle: ((tripDataForNotification: TripDataForNotification, isEnabled: Boolean) -> Any?)) {
        _onLandMarkNotificationToggle = onLandMarkNotificationToggle
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
                    ),
                    ImageSliderAdapter(appContext)
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
        private val binding: LayoutTravelSpotTimelineBinding,
        private val adapterImageSlider: ImageSliderAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, travelPlace: TravelPlace) {
            val timeLineType =
                if (position == 0) 1 else if (position == _mFeedList.size - 1) 2 else 0
            binding.apply {
                val placeUiModel = travelPlace.placeDetails
                root.setOnClickListener {
                    placeUiModel?.placeId?.let { it1 -> _onLandMarkSelectedListener?.invoke(it1) }
                }
                timeline.initLine(timeLineType)
                binding.vpPlaceImages.adapter = adapterImageSlider
                binding.vpPlaceImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                placeUiModel?.photos?.let { adapterImageSlider.setPlacePhotos(it) }
                TabLayoutMediator(binding.imageIndicators, binding.vpPlaceImages) { tab, position ->

                }.attach()
                tvTimeSlot.text = travelPlace.travelTiming
                tvPlaceName.text = placeUiModel?.name
                placeRating.rating = placeUiModel?.rating?.toFloat() ?: 0f
                tvRating.text = "${placeUiModel?.reviewsCount} reviews"
                btnNavigate.setOnClickListener {
                    openDirection(
                        appContext, placeUiModel?.name, LatLng(
                            placeUiModel?.latLng?.latitude ?: 0.0,
                            placeUiModel?.latLng?.longitude ?: 0.0
                        )
                    )
                }
                ivToggleTripNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                    travelPlace.travelDateAndTiming?.let {
                        _onLandMarkNotificationToggle?.invoke(
                            TripDataForNotification(
                                placeId = placeUiModel?.placeId.toString(),
                                placeName = placeUiModel?.name.toString(),
                                placeImage = placeUiModel?.photos?.firstOrNull(),
                                notificationDateAndTime = "2023-11-10 14:35"
                            ), isChecked
                        )
                    }
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