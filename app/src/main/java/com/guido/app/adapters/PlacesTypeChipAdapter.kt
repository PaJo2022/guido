package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guido.app.R
import com.guido.app.databinding.LayoutPlaceTypeChipItemBinding
import com.guido.app.model.PlaceType

class PlacesTypeChipAdapter(private val appContext : Context) : RecyclerView.Adapter<PlacesTypeChipAdapter.PlaceTypeChipViewHolder>() {

    private var _types: List<PlaceType> = ArrayList()

    fun setPlacesType(types: List<PlaceType>) {
        _types = types
        notifyDataSetChanged()
    }

    private var onItemClickListener : ((PlaceType) -> Any?)? =null

    fun setOnPlaceTypeSelected(onItemClickListener : ((PlaceType) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlaceTypeChipViewHolder(private val binding: LayoutPlaceTypeChipItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceType) {
            binding.apply {
                root.apply {
                    backgroundTintList =  appContext.getColorStateList(if(type.isSelected) type.selectedColor else  R.color.card_color1)
                    setOnClickListener {
                        onItemClickListener?.invoke(type)
                    }
                }
                tvPlaceTypeName. text = type.displayName

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceTypeChipViewHolder(
        LayoutPlaceTypeChipItemBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun getItemCount() = _types.size

    override fun onBindViewHolder(holder: PlaceTypeChipViewHolder, position: Int) {
        holder.bindItem(_types[position])
    }
}