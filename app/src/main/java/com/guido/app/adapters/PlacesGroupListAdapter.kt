package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.toUpperCase
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.guido.app.databinding.LayoutPlaceGroupItemBinding
import com.guido.app.databinding.LayoutPlaceItemBinding
import com.guido.app.databinding.LayoutPlaceListItemBinding
import com.guido.app.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.guido.app.model.placesUiModel.PlaceTypeUiModel
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.videosUiModel.VideoUiModel
import java.util.Locale

class PlacesGroupListAdapter(private val appContext : Context) : RecyclerView.Adapter<PlacesGroupListAdapter.PlacesListAdapterViewHolder>() {

    private var _placeGroupItem: List<PlaceTypeUiModel> = DUMMY_PLACE_TYPE_UI_MODEL

    fun setNearByPlaces(placeGroupItem: List<PlaceTypeUiModel>) {
        _placeGroupItem = placeGroupItem
        notifyDataSetChanged()
    }

    fun clear(){
        _placeGroupItem = DUMMY_PLACE_TYPE_UI_MODEL
        notifyDataSetChanged()
    }

    private var onItemClickListener : ((PlaceUiModel) -> Any?)? =null

    fun setOnLandMarkClicked(onItemClickListener : ((PlaceUiModel) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceTypeUiModel) {
            val placesAdapter = PlacesListAdapter(appContext)
            binding.apply {

                tvPlaceType.text = place.type.toString().uppercase(Locale.ENGLISH)
                rvPlaces.apply {
                    adapter = placesAdapter
                    layoutManager = LinearLayoutManager(appContext,LinearLayoutManager.VERTICAL,false)
                }
                place.places?.let { placesAdapter.setNearByPlaces(it) }
                placesAdapter.setOnLandMarkClicked {place->
                    onItemClickListener?.invoke(place)
                }
             //   Glide.with(appContext).load(place.icon).centerCrop().into(ivPlaceTypeIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlacesListAdapterViewHolder(
        LayoutPlaceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = _placeGroupItem.size

    override fun onBindViewHolder(holder: PlacesListAdapterViewHolder, position: Int) {
        holder.bindItem(_placeGroupItem[position])
    }
}