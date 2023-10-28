package com.innoappsai.guido.generateItinerary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelTimeDistributionBinding
import com.innoappsai.guido.generateItinerary.model.TravelItineraryGenerationOption

class AdapterItineraryGenerationOptions : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _options: List<TravelItineraryGenerationOption> = emptyList()

    fun setItineraryOptions(options: List<TravelItineraryGenerationOption>) {
        _options = options
        notifyDataSetChanged()
    }

    inner class AdapterItineraryGenerationTravelDurationOptionViewHolder(private val binding: LayoutItinearyGenerationTravelDurationBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    inner class AdapterItineraryGenerationTravelEachDayTravelTimeOptionViewHolder(private val binding: LayoutItinearyGenerationTravelTimeDistributionBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            viewType == TravelItineraryGenerationOption.TRAVEL_DURATION.ordinal -> {
                AdapterItineraryGenerationTravelDurationOptionViewHolder(
                    LayoutItinearyGenerationTravelDurationBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == TravelItineraryGenerationOption.TRAVEL_TIME_ALLOCATION.ordinal -> {
                AdapterItineraryGenerationTravelEachDayTravelTimeOptionViewHolder(
                    LayoutItinearyGenerationTravelTimeDistributionBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else ->
        }
    }

    override fun getItemCount() = _options.size


    override fun getItemViewType(position: Int): Int {
        return _options[position].ordinal
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}