package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.calculateDistance
import com.innoappsai.guido.databinding.ViewpagerCardPlaceItemBinding
import com.innoappsai.guido.formatDouble
import com.innoappsai.guido.getScreenHeight
import com.innoappsai.guido.getScreenWidth
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.updateApiKey

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

                Glide.with(appContext).load(updateApiKey(customObject)).centerCrop().into(ivPlace)
                tvPlaceName.text = place.name
                tvPlaceName.isSelected = true
                tvPlaceIsOpen.apply {
                    text = if(place.isOpenNow) "Open" else "Closed"
                    val textColor = ContextCompat.getColor(appContext, if(place.isOpenNow) R.color.color_sound_compose_selected_color3 else R.color.white)
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

    private fun getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel: PlaceUiModel?): String {
        val placeLatLng = placeUiModel?.latLng ?: return ""

        val myPlaceLatLng = MyApp.userCurrentLatLng ?: return ""

        val totalDistance = calculateDistance(myPlaceLatLng.latitude,myPlaceLatLng.longitude,placeLatLng.latitude,placeLatLng.longitude) / 1000
        return "${formatDouble(totalDistance)} Km"
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