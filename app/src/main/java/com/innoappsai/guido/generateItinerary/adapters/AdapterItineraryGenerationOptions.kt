package com.innoappsai.guido.generateItinerary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelBudgetBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelCompanionsBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDateBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelInterestsBinding
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

        fun bind() {

        }
    }

    inner class AdapterItineraryGenerationTravelEachDayTravelTimeOptionViewHolder(private val binding: LayoutItinearyGenerationTravelTimeDistributionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    inner class AdapterItineraryGenerationTravelDatesOptionViewHolder(private val binding: LayoutItinearyGenerationTravelDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    inner class AdapterItineraryGenerationTravelGroupTypeOptionViewHolder(private val binding: LayoutItinearyGenerationTravelCompanionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    inner class AdapterItineraryGenerationTravelInterestsOptionViewHolder(private val binding: LayoutItinearyGenerationTravelInterestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }


    inner class AdapterItineraryGenerationTravelBudgetTypeOptionViewHolder(private val binding: LayoutItinearyGenerationTravelBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
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

            viewType == TravelItineraryGenerationOption.TRAVEL_DATE.ordinal -> {
                AdapterItineraryGenerationTravelDatesOptionViewHolder(
                    LayoutItinearyGenerationTravelDateBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == TravelItineraryGenerationOption.TRAVEL_COMPANION.ordinal -> {
                AdapterItineraryGenerationTravelGroupTypeOptionViewHolder(
                    LayoutItinearyGenerationTravelCompanionsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == TravelItineraryGenerationOption.TRAVEL_INTERESTS.ordinal -> {
                AdapterItineraryGenerationTravelInterestsOptionViewHolder(
                    LayoutItinearyGenerationTravelInterestsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == TravelItineraryGenerationOption.TRAVEL_BUDGET.ordinal -> {
                AdapterItineraryGenerationTravelBudgetTypeOptionViewHolder(
                    LayoutItinearyGenerationTravelBudgetBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }


            else -> throw Exception("No Layout Inflated")
        }
    }

    override fun getItemCount() = _options.size


    override fun getItemViewType(position: Int): Int {
        return _options[position].ordinal
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdapterItineraryGenerationTravelDurationOptionViewHolder -> {
                holder.bind()
            }

            is AdapterItineraryGenerationTravelEachDayTravelTimeOptionViewHolder -> {
                holder.bind()
            }

            is AdapterItineraryGenerationTravelDatesOptionViewHolder -> {
                holder.bind()
            }

            is AdapterItineraryGenerationTravelGroupTypeOptionViewHolder -> {
                holder.bind()
            }

            is AdapterItineraryGenerationTravelInterestsOptionViewHolder -> {
                holder.bind()
            }

            is AdapterItineraryGenerationTravelBudgetTypeOptionViewHolder -> {
                holder.bind()
            }
        }
    }
}