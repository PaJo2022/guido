package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guido.app.databinding.LayoutPlaceGroupItemBinding
import com.guido.app.model.PlaceType
import com.guido.app.model.PlaceTypeContainer

class PlacesTypeGroupAdapter(private val appContext: Context) :
    RecyclerView.Adapter<PlacesTypeGroupAdapter.PlaceTypeGroupViewHolder>() {

    private var _types: List<PlaceTypeContainer> = ArrayList()

    fun setPlacesType(types: List<PlaceTypeContainer>) {
        _types = types
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceType) -> Any?)? = null

    fun setOnPlaceTypeSelected(onItemClickListener: ((PlaceType) -> Any?)) {
        this.onItemClickListener = onItemClickListener
    }

    inner class PlaceTypeGroupViewHolder(private val binding: LayoutPlaceGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceTypeContainer) {
            val placeAdapter = PlacesTypeChipAdapter(appContext)
            binding.apply {
                tvPlaceType.text = type.type
                rvPlaces.apply {
                    layoutManager =
                        GridLayoutManager(appContext, 3, GridLayoutManager.VERTICAL, false)
                    adapter = placeAdapter
                }
                placeAdapter.setPlacesType(type.placeTypes)
                placeAdapter.setOnPlaceTypeSelected {
                    onItemClickListener?.invoke(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceTypeGroupViewHolder(
        LayoutPlaceGroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun getItemCount() = _types.size

    override fun onBindViewHolder(holder: PlaceTypeGroupViewHolder, position: Int) {
        holder.bindItem(_types[position])
    }
}