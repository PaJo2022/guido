package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.databinding.LayoutPlacesItemBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel

class PlacesListAdapter(private val appContext : Context) : RecyclerView.Adapter<PlacesListAdapter.PlacesListAdapterViewHolder>() {

    private var _places: List<PlaceUiModel> = ArrayList()

    fun setNearByPlaces(places: List<PlaceUiModel>) {
        _places = places
        notifyDataSetChanged()
    }

    private var onItemClickListener : ((PlaceUiModel) -> Any?)? =null

    fun setOnLandMarkClicked(onItemClickListener : ((PlaceUiModel) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlacesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceUiModel) {
            binding.apply {
                parent.setOnClickListener {
                    onItemClickListener?.invoke(place)
                }
                tvPlaceName.text = place.name
                Glide.with(appContext).load(place.icon).into(ivPlaces)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlacesListAdapterViewHolder(
        LayoutPlacesItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun getItemCount() = _places.size

    override fun onBindViewHolder(holder: PlacesListAdapterViewHolder, position: Int) {
        holder.bindItem(_places[position])
    }
}