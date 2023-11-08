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
                val mapLogo =
                    "https://firebasestorage.googleapis.com/v0/b/guido-ble.appspot.com/o/places_map_static_image%2F0d467727-7031-41d1-baa4-d1055b033cb6?alt=media&token=b14454d8-befd-4f11-a7c9-8f6032dd4659&_gl=1*143t7sl*_ga*NzYzNDIwMjQ1LjE2OTkzMzY1Mzc.*_ga_CW55HF8NVT*MTY5OTM3NDc5OC42LjEuMTY5OTM3NDgxMy40NS4wLjA."
                Glide.with(appContext).load(mapLogo).centerCrop().into(ivTravelMap)
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