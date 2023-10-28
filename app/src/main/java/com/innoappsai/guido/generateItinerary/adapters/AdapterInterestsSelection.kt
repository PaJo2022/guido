package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutTravelInterestsSelectionBinding
import com.innoappsai.guido.generateItinerary.model.InterestsSelection

class AdapterInterestsSelection(private val appContext: Context) :
    RecyclerView.Adapter<AdapterInterestsSelection.AdapterInterestsSelectionViewHolder>() {

    private var _interestsOption: List<InterestsSelection> = emptyList()

    fun setInterestsOptions(dayWiseTime: List<InterestsSelection>) {
        _interestsOption = dayWiseTime
        notifyDataSetChanged()
    }

    private var _onSliderChangeListener: ((id: String, value: Float) -> Any?)? =
        null

    fun setOnSliderChangeListener(onSliderChangeListener: ((id: String, value: Float) -> Any?)) {
        _onSliderChangeListener = onSliderChangeListener
    }

    inner class AdapterInterestsSelectionViewHolder(private val binding: LayoutTravelInterestsSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InterestsSelection) {
            binding.apply {
                tvInterestsName.text = item.interestsName
                ivInterestsIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        appContext,
                        item.interestsIcon
                    )
                )
                slider.addOnChangeListener { slider, value, fromUser ->
                    if (fromUser) {
                        tvInterestsLevel.text = when {
                            value == 0f -> "Not Interested"
                            value == 2f -> "Curious"
                            value == 4f -> "Interested"
                            value == 6f -> "Passionate"
                            value == 8f -> "Must Have"
                            else -> "Interested"
                        }
                        _onSliderChangeListener?.invoke(item.id, value)

                    }
                }

            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterInterestsSelectionViewHolder {
        return AdapterInterestsSelectionViewHolder(
            LayoutTravelInterestsSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = _interestsOption.size


    override fun onBindViewHolder(holder: AdapterInterestsSelectionViewHolder, position: Int) {
        val item = _interestsOption[position]
        holder.bind(item)
    }
}