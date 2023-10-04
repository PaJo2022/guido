package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlaceDateTimeBinding
import com.innoappsai.guido.databinding.LayoutPlaceFeatureBinding
import com.innoappsai.guido.model.PlaceFeature

class PlaceDateTimeAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceDateTimeAdapter.PlaceDateTimeViewHolder>() {


    private var _placeDateOrTime: List<String> = ArrayList()

    fun setPlaceDateOrTime(placeDateOrTime: List<String>) {
        _placeDateOrTime = placeDateOrTime
        notifyDataSetChanged()
    }

    private var _onPlaceDateOrTimeSelected: ((String) -> Any?)? = null

    fun setOnPlaceDateOrTimeSelected(onPlaceDateOrTimeSelected: ((String) -> Any?)) {
        _onPlaceDateOrTimeSelected = onPlaceDateOrTimeSelected
    }

    inner class PlaceDateTimeViewHolder(private val binding: LayoutPlaceDateTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(dateOrTime : String) {
            binding.apply {
                tvPlaceDateTime.text = dateOrTime
                root.setOnClickListener {
                    _onPlaceDateOrTimeSelected?.invoke(dateOrTime)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceDateTimeViewHolder(
        LayoutPlaceDateTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeDateOrTime.size

    override fun onBindViewHolder(holder: PlaceDateTimeViewHolder, position: Int) {
        val data = _placeDateOrTime[position]

        holder.bindItem(data)
    }
}