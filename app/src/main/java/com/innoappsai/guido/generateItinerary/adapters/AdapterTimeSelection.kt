package com.innoappsai.guido.generateItinerary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.LabelFormatter
import com.innoappsai.guido.convertToAMPM
import com.innoappsai.guido.databinding.LayoutTravelTimeSelectionBinding
import com.innoappsai.guido.generateItinerary.model.DayWiseTimeSelection

class AdapterTimeSelection :
    RecyclerView.Adapter<AdapterTimeSelection.AdapterTimeSelectionViewHolder>() {

    private var _dayWiseTime: List<DayWiseTimeSelection> = emptyList()

    fun setItineraryOptions(dayWiseTime: List<DayWiseTimeSelection>) {
        _dayWiseTime = dayWiseTime
        notifyDataSetChanged()
    }

    private var _onSliderChangeListener: ((id: String, startTime: Float, endTime: Float) -> Any?)? =
        null

    fun setOnSliderChangeListener(onSliderChangeListener: ((id: String, startTime: Float, endTime: Float) -> Any?)) {
        _onSliderChangeListener = onSliderChangeListener
    }

    inner class AdapterTimeSelectionViewHolder(private val binding: LayoutTravelTimeSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DayWiseTimeSelection) {
            binding.apply {
                tvDay.text = item.dayName

                //If you only want the slider start and end value and don't care about the previous values
                rangeSlider.setLabelFormatter {
                    convertToAMPM(it.toInt() - 1)
                }
                rangeSlider.addOnChangeListener { slider, value, fromUser ->
                    val values = rangeSlider.values
                    if (fromUser) {
                        tvDayTiming.text =
                            "${convertToAMPM(values[0].toInt() - 1)} - ${convertToAMPM(values[1].toInt() - 1)}"

                        _onSliderChangeListener?.invoke(
                            item.id,
                            values[0],
                            values[1]
                        )
                    }
                }


            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTimeSelectionViewHolder {
        return AdapterTimeSelectionViewHolder(
            LayoutTravelTimeSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = _dayWiseTime.size


    override fun onBindViewHolder(holder: AdapterTimeSelectionViewHolder, position: Int) {
        val item = _dayWiseTime[position]
        holder.bind(item)
    }
}