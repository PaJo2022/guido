package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlaceListItemBinding
import com.innoappsai.guido.model.placesUiModel.DUMMY_PLACE_TYPE_UI_MODEL
import com.innoappsai.guido.model.placesUiModel.PlaceTypeUiModel
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import java.util.Locale

class PlacesGroupListAdapter(private val appContext : Context) :
    RecyclerView.Adapter<PlacesGroupListAdapter.PlacesListAdapterViewHolder>() {

    private var _placeGroupItem: List<PlaceTypeUiModel> = DUMMY_PLACE_TYPE_UI_MODEL

    fun setNearByPlaces(placeGroupItem: List<PlaceTypeUiModel>) {
        _placeGroupItem = placeGroupItem
        notifyDataSetChanged()
    }

    fun clear(){
        _placeGroupItem = DUMMY_PLACE_TYPE_UI_MODEL
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceUiModel) -> Any?)? = null

    fun setOnLandMarkClicked(onItemClickListener: ((PlaceUiModel) -> Any?)) {
        this.onItemClickListener = onItemClickListener
    }


    private var onLandMarkCheckBoxClicked: ((PlaceUiModel, Boolean) -> Any?)? = null

    fun setOnLandMarkCheckBoxClicked(onLandMarkCheckBoxClicked: ((PlaceUiModel, Boolean) -> Any?)) {
        this.onLandMarkCheckBoxClicked = onLandMarkCheckBoxClicked
    }


    inner class PlacesListAdapterViewHolder(private val binding: LayoutPlaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceTypeUiModel) {
            val placesAdapter = PlacesListAdapter(appContext)
            binding.apply {

                tvPlaceType.text = place.type.toString().uppercase(Locale.ENGLISH)
                rvPlaces.apply {
                    adapter = placesAdapter
                    layoutManager =
                        LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false)
                }
                place.places?.let { placesAdapter.setNearByPlaces(it) }
                placesAdapter.setOnLandMarkClicked { place ->
                    onItemClickListener?.invoke(place)
                }
                placesAdapter.setOnLandMarkCheckBoxClicked { placeUiModel, b ->
                    onLandMarkCheckBoxClicked?.invoke(placeUiModel, b)
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