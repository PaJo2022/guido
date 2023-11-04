package com.innoappsai.guido.generateItinerary.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutTravelDateItemBinding
import com.innoappsai.guido.generateItinerary.model.itinerary.TripData
import java.text.SimpleDateFormat
import java.util.Locale


class AdapterTravelDate(private val appContext : Context) :
    RecyclerView.Adapter<AdapterTravelDate.AdapterTravelDateSelectionViewHolder>() {

    private var _tripData: List<TripData> = emptyList()

    fun setData(tripData: List<TripData>) {
        _tripData = tripData
        notifyDataSetChanged()
    }

    private var _onTravelDateSelectedListener: ((day : String) -> Any?)? =
        null

    fun setOnTravelDateClickListener(onTravelDateSelectedListener: ((day : String) -> Any?)) {
        _onTravelDateSelectedListener = onTravelDateSelectedListener
    }

    inner class AdapterTravelDateSelectionViewHolder(private val binding: LayoutTravelDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TripData) {
            val backgroundCardColor = if(item.isSelected) R.color.color_secondary else R.color.white
            val textColor = if(item.isSelected) R.color.white else R.color.color_primary
            binding.apply {
                Log.i("JAPAN", "Date: ${item}")
                bottomSheet.setCardBackgroundColor(ContextCompat.getColor(appContext,backgroundCardColor))
                tvDay.setTextColor(ContextCompat.getColor(appContext,textColor))
                tvDate.setTextColor(ContextCompat.getColor(appContext,textColor))
                tvDay.text = item.day
                tvDate.text = item.date?.let { formatDateToDayAndDate(it) }
                bottomSheet.setOnClickListener {
                    _onTravelDateSelectedListener?.invoke(item.day.toString())
                }
            }
        }
    }

    fun formatDateToDayAndDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd", Locale.getDefault())
        return try {
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTravelDateSelectionViewHolder {
        return AdapterTravelDateSelectionViewHolder(
            LayoutTravelDateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = _tripData.size


    override fun onBindViewHolder(holder: AdapterTravelDateSelectionViewHolder, position: Int) {
        val item = _tripData[position]
        holder.bind(item)
    }
}