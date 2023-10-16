package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutPlaceItemBinding
import com.innoappsai.guido.databinding.LayoutPlaceListCategoryTypeItemBinding
import com.innoappsai.guido.databinding.LayoutPlaceShimmerItemBinding
import com.innoappsai.guido.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.updateApiKey

class PlacesGroupListAdapter(private val appContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _placeGroupItem: List<PlaceTypeUiModel> = DUMMY_PLACE_TYPE_UI_MODEL

    fun setNearByPlaces(placeGroupItem: List<PlaceTypeUiModel>) {
        _placeGroupItem = placeGroupItem
        notifyDataSetChanged()
    }

    fun clear() {
        _placeGroupItem = DUMMY_PLACE_TYPE_UI_MODEL
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceUiModel) -> Any?)? = null

    fun setOnLandMarkClicked(onItemClickListener: ((PlaceUiModel) -> Any?)) {
        this.onItemClickListener = onItemClickListener
    }


    private var onLandMarkCheckBoxClicked: ((PlaceUiModel, Boolean) -> Any?)? = null

    fun setOnLandMarkCheckBoxClicked(onLandMarkCheckBoxClicked: ((PlaceUiModel, Boolean) -> Any?)) {
        this.onLandMarkCheckBoxClicked = onLandMarkCheckBoxClicked
    }

    inner class PlacesCategoryTypeViewHolder(private val binding: LayoutPlaceListCategoryTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val placesAdapter = PlacesListAdapter(appContext)
        fun bindItem(place: PlaceTypeUiModel) {
            binding.apply {
                tvPlaceType.text = place.categoryTitle
                //   Glide.with(appContext).load(place.icon).centerCrop().into(ivPlaceTypeIcon)
            }
        }
    }

    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val placesAdapter = PlacesListAdapter(appContext)
        fun bindItem(placeTypeUiModel: PlaceTypeUiModel) {
            val place = placeTypeUiModel.place ?: return
            binding.apply {
                val customObject = place.photos?.firstOrNull()

                Glide.with(appContext).load(updateApiKey(customObject)).centerCrop()
                    .into(placeImage)
                tvPlaceName.text = place.name
                tvPlaceName.isSelected = true
                tvPlaceIsOpen.apply {
                    text = place.placeOpenStatus
                    val textColor = ContextCompat.getColor(
                        appContext,
                        if (place.isOpenNow) R.color.color_sound_compose_selected_color3 else R.color.color_primary
                    )
                    setTextColor(textColor)
                }
                tvPlaceDescription.text = place.address
                tvPlaceDescription.isSelected = true
                placeRating.rating = place.rating?.toFloat() ?: 0f
                placeRatingText.text = "(${place.reviewsCount ?: 0})"
                //  placeDistance.text = getDistanceBetweenMyPlaceAndTheCurrentPlace(place)
                cbIsCelected.apply {
                    isVisible = place.shouldShowCheckBox
                    isChecked = place.isChecked
                    this.setOnCheckedChangeListener { compoundButton, b ->
                        onLandMarkCheckBoxClicked?.invoke(place, b)
                    }
                }
                root.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }
                //   Glide.with(appContext).load(place.icon).centerCrop().into(ivPlaceTypeIcon)
            }
        }
    }

    inner class PlacesListAdapterShimmerViewHolder(private val binding: LayoutPlaceShimmerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeUiModel: PlaceUiModel) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                PlacesCategoryTypeViewHolder(
                    LayoutPlaceListCategoryTypeItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            1 ->{
                PlacesListAdapterViewHolder(
                    LayoutPlaceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                PlacesListAdapterShimmerViewHolder(
                    LayoutPlaceShimmerItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (_placeGroupItem[position].categoryTitle != null && _placeGroupItem[position].place == null) 0 else if((_placeGroupItem[position].categoryTitle == null && _placeGroupItem[position].place != null)) 1 else 2
    }

    override fun getItemCount() = _placeGroupItem.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlacesCategoryTypeViewHolder) {
            holder.bindItem(_placeGroupItem[position])
        } else if (holder is PlacesListAdapterViewHolder) {
            holder.bindItem(_placeGroupItem[position])
        }
    }
}