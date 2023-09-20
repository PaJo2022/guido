package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.Constants
import com.guido.app.databinding.ViewpagerCardPlaceItemBinding
import com.guido.app.getScreenHeight
import com.guido.app.getScreenWidth
import com.guido.app.model.placesUiModel.PlaceUiModel

class PlacesHorizontalListAdapter(private val appContext: Context) :
    RecyclerView.Adapter<PlacesHorizontalListAdapter.PlacesHorizontalListAdapterViewHolder>() {

    private var _places: List<PlaceUiModel> = ArrayList()

    fun setNearByPlaces(place: List<PlaceUiModel>) {
        _places = place
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceUiModel) -> Any?)? = null

    fun setOnLandMarkClicked(onItemClickListener: ((PlaceUiModel) -> Any?)) {
        this.onItemClickListener = onItemClickListener
    }

    inner class PlacesHorizontalListAdapterViewHolder(private val binding: ViewpagerCardPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceUiModel) {
            binding.apply {

                val screenWidth = appContext.getScreenWidth()
                val screenHeight = appContext.getScreenHeight()

                // Get screen width and height


                // Calculate new dimensions
                val newWidth = (screenWidth * 0.75).toInt()
                val newHeight = (screenHeight / 1.7).toInt()


                // Set ImageView dimensions
                val layoutParams = binding.parentLayout.layoutParams
                layoutParams.width = newWidth
                binding.parentLayout.layoutParams = layoutParams

                val customObject = place.photos?.firstOrNull()
                val photoUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?photoreference=${customObject?.photo_reference}&sensor=false&maxheight=${customObject?.height}&maxwidth=${customObject?.width}&key=${Constants.GCP_API_KEY}"

                Glide.with(appContext).load(photoUrl).centerCrop().into(ivPlace)
                tvPlaceName.text = place.name
                tvPlaceName.isSelected = true
                placeRating.rating = place.rating?.toFloat() ?: 0f
                placeRatingText.text = "(${place.rating ?: 0.0})"

                root.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlacesHorizontalListAdapterViewHolder(
            ViewpagerCardPlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount() = _places.size

    override fun onBindViewHolder(holder: PlacesHorizontalListAdapterViewHolder, position: Int) {
        holder.bindItem(_places[position])
    }
}