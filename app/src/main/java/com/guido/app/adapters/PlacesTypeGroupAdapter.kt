package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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

    private var onIntrestSectionOpened: (() -> Any?)? = null

    fun setOnInterestSectionOpen(onIntrestSectionOpened: (() -> Any?)) {
        this.onIntrestSectionOpened = onIntrestSectionOpened
    }

    inner class PlaceTypeGroupViewHolder(private val binding: LayoutPlaceGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceTypeContainer) {
            val placeAdapter = PlacesTypeChipAdapter(appContext)
            binding.apply {
                tvPlaceType.text = type.type  + " (+${type.placeTypes.size})"
                placeAdapter.setPlacesType(type.placeTypes)
                placeAdapter.setOnPlaceTypeSelected {

                }
                if(type.isOpened){
                    motionLayout.transitionToEnd()
                }else{
                    motionLayout.transitionToStart()
                }
                tvPlaceArrow.setOnClickListener {
                    type.isOpened = !type.isOpened
                    if(type.isOpened){
                        motionLayout.transitionToEnd()
                    }else{
                        motionLayout.transitionToStart()
                    }
                    if(type.isOpened){
                        onIntrestSectionOpened?.invoke()
                    }
                    tvPlaceType.text = type.type + if(type.isOpened) "" else " (+${type.placeTypes.size})"
                }


                type.placeTypes.forEachIndexed {index,it->
                    addChips(binding.chipGroupPlaces, it,index) { type ->
                        onItemClickListener?.invoke(type)
                    }
                }
            }
        }
    }

    private fun addChips(
        chipGroup: ChipGroup,
        type: PlaceType,
        index : Int,
        onChipClickListener: (type: PlaceType) -> Unit
    ) {

        Chip(appContext).apply {
            text = type.displayName
            isCloseIconVisible = false
            setOnClickListener {
                onChipClickListener(type)
            }
            chipGroup.addView(this)
        }
        chipGroup.applyCheckedOnSelectedChip(index,type.isSelected)
    }
    private fun ChipGroup.applyCheckedOnSelectedChip(index : Int,isSelected : Boolean){
        val chip:Chip = this.getChildAt(index) as Chip
        chip.isChecked = isSelected
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceTypeGroupViewHolder(
        LayoutPlaceGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = _types.size

    override fun onBindViewHolder(holder: PlaceTypeGroupViewHolder, position: Int) {
        holder.bindItem(_types[position])
    }
}