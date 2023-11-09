package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.TravelItinerary
import com.innoappsai.guido.databinding.LayoutTravelItineraryItemBinding

class AdapterItineraryList(private val appContext: Context) :
    RecyclerView.Adapter<AdapterItineraryList.ItineraryViewHolder>() {

    private var _itineraryList: List<TravelItinerary?> = emptyList()

    fun setItineraryList(itineraryList: List<TravelItinerary?>) {
        _itineraryList = itineraryList
        notifyDataSetChanged()
    }


    private var onItemClickListener: ((id: String) -> Any?)? = null

    fun setOnItemClicked(onItemClickListener: ((id: String) -> Any?)) {
        this.onItemClickListener = onItemClickListener
    }


    inner class ItineraryViewHolder(private val binding: LayoutTravelItineraryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(travelItinerary: TravelItinerary) {
            binding.apply {
                tvTitle.text =
                    "${travelItinerary.itineraryModel?.placeName},${travelItinerary.itineraryModel?.countryName}"
                tvDescription.text =
                    "Check Your Next Travel At ${travelItinerary.itineraryModel?.placeName}"
                 Glide.with(appContext).load(travelItinerary.itineraryModel?.placeMapUrl).centerCrop().into(ivTravelMap)
                root.setOnClickListener {
                    onItemClickListener?.invoke(travelItinerary.id)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItineraryViewHolder {
        return ItineraryViewHolder(
            LayoutTravelItineraryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount() = _itineraryList.size

    override fun onBindViewHolder(holder: ItineraryViewHolder, position: Int) {
        val item = _itineraryList[position] ?: return
        holder.bindItem(item)
    }
}