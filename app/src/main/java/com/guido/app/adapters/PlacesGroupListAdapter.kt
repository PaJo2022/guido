package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.guido.app.databinding.LayoutPlaceGroupItemBinding
import com.guido.app.databinding.LayoutPlaceItemBinding
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel

class PlacesGroupListAdapter(private val appContext : Context) : RecyclerView.Adapter<PlacesGroupListAdapter.PlacesListAdapterViewHolder>() {

    private var _placeGroupItem: List<PlaceTypeUiModel> = ArrayList()

    fun setNearByPlaces(placeGroupItem: List<PlaceTypeUiModel>) {
        _placeGroupItem = placeGroupItem
        notifyDataSetChanged()
    }

    private var onItemClickListener : ((PlaceUiModel) -> Any?)? =null

    fun setOnLandMarkClicked(onItemClickListener : ((PlaceUiModel) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlaceGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceTypeUiModel) {
            val placesAdapter = PlacesListAdapter(appContext)
            binding.apply {
                rightImageView.setOnClickListener {
                    rvPlaces.isVisible = !rvPlaces.isVisible
                    rightImageView.rotation = if(rvPlaces.isVisible) -90f else 90f
                }
                tvPlaceType.text = place.type
                Glide.with(appContext).load(place.icon).into(placeIcon)
                rvPlaces.apply {
                    adapter = placesAdapter
                    layoutManager = LinearLayoutManager(appContext,LinearLayoutManager.VERTICAL,false)
                }
                place.places?.let { placesAdapter.setNearByPlaces(it) }
                placesAdapter.setOnLandMarkClicked {place->
                    onItemClickListener?.invoke(place)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlacesListAdapterViewHolder(
        LayoutPlaceGroupItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun getItemCount() = _placeGroupItem.size

    override fun onBindViewHolder(holder: PlacesListAdapterViewHolder, position: Int) {
        holder.bindItem(_placeGroupItem[position])
    }
}