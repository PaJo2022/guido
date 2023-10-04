package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlaceTimingsBinding
import com.innoappsai.guido.model.PlaceTimings

class PlaceTimingsAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceTimingsAdapter.PlaceTimingsViewHolder>() {


    private var _placeTimings: List<PlaceTimings> = ArrayList()

    fun setPlaceFeatures(placeTimings: List<PlaceTimings>) {
        _placeTimings = placeTimings
        notifyDataSetChanged()
    }

    private var _onPlaceTimingDelete: ((String) -> Any?)? = null

    fun setOnPlaceTimingDelete(onPlaceTimingDelete: ((String) -> Any?)) {
        _onPlaceTimingDelete = onPlaceTimingDelete
    }

    inner class PlaceTimingsViewHolder(private val binding: LayoutPlaceTimingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeTimings: PlaceTimings) {
            binding.apply {
                tvPlaceTiming.text =
                    "${placeTimings.dayOfTheWeek} ${placeTimings.from} - ${placeTimings.to}"
                ivDeletePlaceTimings.setOnClickListener {
                    _onPlaceTimingDelete?.invoke(placeTimings.id)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceTimingsViewHolder(
        LayoutPlaceTimingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeTimings.size

    override fun onBindViewHolder(holder: PlaceTimingsViewHolder, position: Int) {
        val data = _placeTimings[position]

        holder.bindItem(data)
    }
}