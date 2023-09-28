package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.Constants
import com.guido.app.MyApp
import com.guido.app.R
import com.guido.app.calculateDistance
import com.guido.app.databinding.LayoutPlaceItemBinding
import com.guido.app.databinding.LayoutPlaceShimmerItemBinding
import com.guido.app.formatDouble
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiType
import com.guido.app.updateApiKey

class PlacesListAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var _places: List<PlaceUiModel> = ArrayList()

    fun setNearByPlaces(places: List<PlaceUiModel>) {
        _places = places
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceUiModel) -> Any?)? = null

    fun setOnLandMarkClicked(onItemClickListener : ((PlaceUiModel) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceUiModel) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }

            }
        }
    }

    inner class PlacesVerticalListAdapterViewHolder(private val binding: LayoutPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceUiModel) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }

                val customObject = place.photos?.firstOrNull()

                Glide.with(appContext).load(updateApiKey(customObject)).centerCrop().into(placeImage)
                tvPlaceName.text = place.name
                tvPlaceName.isSelected = true
                tvPlaceIsOpen.apply {
                    text = if(place.isOpenNow) "Open" else "Closed"
                    val textColor = ContextCompat.getColor(appContext, if(place.isOpenNow) R.color.color_sound_compose_selected_color3 else R.color.color_primary)
                    setTextColor(textColor)
                }
                tvPlaceDescription.text = place.address
                tvPlaceDescription.isSelected = true
                placeRating.rating = place.rating?.toFloat() ?: 0f
                placeRatingText.text = "(${place.rating ?: 0.0})"
                placeDistance.text = getDistanceBetweenMyPlaceAndTheCurrentPlace(place)
                root.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }

            }
        }
    }

    inner class PlacesListAdapterShimmerViewHolder(private val binding: LayoutPlaceShimmerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeUiModel: PlaceUiModel) {

        }

    }

    private fun getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel: PlaceUiModel?): String {
        val placeLatLng = placeUiModel?.latLng ?: return ""

        val myPlaceLatLng = MyApp.userCurrentLatLng ?: return ""

        val totalDistance = calculateDistance(myPlaceLatLng.latitude,myPlaceLatLng.longitude,placeLatLng.latitude,placeLatLng.longitude) / 1000
        return "${formatDouble(totalDistance)} Km"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == PlaceUiType.LARGE.ordinal) {
            PlacesVerticalListAdapterViewHolder(
                LayoutPlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else if (viewType == PlaceUiType.LARGE_SHIMMER.ordinal) {
            PlacesListAdapterShimmerViewHolder(
                LayoutPlaceShimmerItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            PlacesListAdapterViewHolder(
                LayoutPlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    override fun getItemCount() = _places.size

    override fun getItemViewType(position: Int): Int {
        return _places[position].placeUiType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlacesVerticalListAdapterViewHolder) {
            holder.bindItem(_places[position])
        } else if (holder is PlacesListAdapterViewHolder) {
            holder.bindItem(_places[position])
        } else if (holder is PlacesListAdapterShimmerViewHolder) {
            holder.bindItem(_places[position])
        }
    }
}