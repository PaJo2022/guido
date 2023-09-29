package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.innoappsai.guido.databinding.LayoutPlaceGroupItemBinding
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.PlaceTypeContainer

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
            binding.apply {
                tvPlaceType.text = type.type  + " (+${type.placeTypes.size})"
                if(type.isOpened){
                    rotateViewBy(ivPlaceArrow,270f)
                    motionLayout.transitionToEnd()
                }else{
                    rotateViewBy(ivPlaceArrow,90f)
                    motionLayout.transitionToStart()
                }
                llPlaceType.setOnClickListener {
                    type.isOpened = !type.isOpened
                    if(type.isOpened){
                        rotateViewBy(ivPlaceArrow,270f)
                        motionLayout.transitionToEnd()
                    }else{
                        rotateViewBy(ivPlaceArrow,90f)
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

    fun rotateViewBy(view : View,toDegree : Float){
        view.rotation = toDegree
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