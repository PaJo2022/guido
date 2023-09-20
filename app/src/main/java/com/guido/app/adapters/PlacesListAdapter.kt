package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.Constants
import com.guido.app.databinding.LayoutPlaceItemBinding
import com.guido.app.databinding.LayoutPlaceShimmerItemBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.PlaceUiType

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
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?photoreference=${customObject?.photo_reference}&sensor=false&maxheight=${customObject?.height}&maxwidth=${customObject?.width}&key=${Constants.GCP_API_KEY}"

                Glide.with(appContext).load(photoUrl).centerCrop().into(placeImage)
                tvPlaceName.text = place.name
                tvPlaceName.isSelected = true
                placeRating.rating = place.rating?.toFloat() ?: 0f
                placeRatingText.text = "(${place.rating ?: 0.0})"

            }
        }
    }

    inner class PlacesListAdapterShimmerViewHolder(private val binding: LayoutPlaceShimmerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeUiModel: PlaceUiModel) {

        }

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