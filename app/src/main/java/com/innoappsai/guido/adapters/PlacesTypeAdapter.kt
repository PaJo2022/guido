package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutPlaceTypeItemBinding
import com.innoappsai.guido.model.PlaceType

class PlacesTypeAdapter(private val appContext: Context) :
    RecyclerView.Adapter<PlacesTypeAdapter.PlaceTypeChipViewHolder>() {

    private var _types: List<PlaceType> = ArrayList()

    fun setPlacesType(types: List<PlaceType>) {
        _types = types
        notifyDataSetChanged()
    }

    private var onItemClickListener: ((PlaceType) -> Any?)? = null

    fun setOnPlaceTypeSelected(onItemClickListener : ((PlaceType) -> Any?)){
        this.onItemClickListener = onItemClickListener
    }

    inner class PlaceTypeChipViewHolder(private val binding: LayoutPlaceTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceType) {
            binding.apply {
                root.apply {
                    backgroundTintList =
                        appContext.getColorStateList(if (type.isSelected) type.selectedColor else R.color.card_color1)
                    setOnClickListener {
                        onItemClickListener?.invoke(type)
                    }
                }
                tvPlaceType.text = type.displayName
                cbPlaceType.isChecked = type.isSelected

                cbPlaceType.setOnCheckedChangeListener { compoundButton, b ->
                    onItemClickListener?.invoke(type)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceTypeChipViewHolder(
        LayoutPlaceTypeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = _types.size

    override fun onBindViewHolder(holder: PlaceTypeChipViewHolder, position: Int) {
        holder.bindItem(_types[position])
    }
}