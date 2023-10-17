package com.innoappsai.guido.adapters

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutPlaceFilterViewBinding
import com.innoappsai.guido.model.PlaceFilter.PlaceFilter
import com.innoappsai.guido.model.PlaceFilter.PlaceFilterType
import com.innoappsai.guido.model.PlaceFilter.placeFiltersList

class PlaceFilterHorizontalAdapter(private val appContext: Context) :
    RecyclerView.Adapter<PlaceFilterHorizontalAdapter.PlaceFilterHorizontalAdapterViewHolder>() {

    private var _places: List<PlaceFilter> = placeFiltersList

    fun setFilters(filters: ArrayList<PlaceFilter>) {
        _places = filters
        notifyDataSetChanged()
    }


    private var onFilterItemClicked: ((PlaceFilter) -> Any?)? = null

    fun setOnFilterItemClicked(onFilterItemClicked: ((PlaceFilter) -> Any?)) {
        this.onFilterItemClicked = onFilterItemClicked
    }

    inner class PlaceFilterHorizontalAdapterViewHolder(private val binding: LayoutPlaceFilterViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeFilter: PlaceFilter) {
            binding.apply {
                val colorStateList = if (placeFilter.isSelected) {
                    if (placeFilter.placeFilterType == PlaceFilterType.HYPER_LOCAL_PLACE_SEARCH) {
                        ContextCompat.getColorStateList(appContext, R.color.color_secondary)
                    } else {
                        ContextCompat.getColorStateList(appContext, R.color.color_primary)
                    }
                } else {
                    ContextCompat.getColorStateList(appContext, R.color.color_light_grey)
                }

                val iconTintColor = if (placeFilter.isSelected) {
                    ContextCompat.getColor(appContext, R.color.white)
                } else {
                    ContextCompat.getColor(appContext, R.color.color_primary)
                }

                val textColor = if (placeFilter.isSelected) {
                    ContextCompat.getColor(appContext, R.color.white)
                } else {
                    ContextCompat.getColor(appContext, R.color.color_primary)
                }

                parentLayout.backgroundTintList = colorStateList
                tvFilter.setTextColor(textColor)
                ivLeftIcon.setColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)
                ivRightIcon.setColorFilter(iconTintColor, PorterDuff.Mode.SRC_IN)

                placeFilter.leftIcon?.let {
                    ivLeftIcon.isVisible = true
                    ivLeftIcon.setImageResource(it)
                }

                placeFilter.title?.let {
                    tvFilter.isVisible = true
                    tvFilter.text = it
                }

                placeFilter.rightIcon?.let {
                    ivRightIcon.isVisible = true
                    ivRightIcon.setImageResource(it)
                }
                parentLayout.setOnClickListener {
                    onFilterItemClicked?.invoke(placeFilter)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceFilterHorizontalAdapterViewHolder(
            LayoutPlaceFilterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount() = _places.size

    override fun onBindViewHolder(holder: PlaceFilterHorizontalAdapterViewHolder, position: Int) {
        holder.bindItem(_places[position])
    }
}