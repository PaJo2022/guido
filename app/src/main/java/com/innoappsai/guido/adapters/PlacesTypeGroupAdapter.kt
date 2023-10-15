package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.innoappsai.guido.databinding.LayoutPlaceGroupItemBinding
import com.innoappsai.guido.databinding.LayoutPlaceGroupItemVerticalBinding
import com.innoappsai.guido.model.PlaceType
import com.innoappsai.guido.model.PlaceTypeContainer

class PlacesTypeGroupAdapter(
    private val appContext: Context,
    private val placeTypeView: PlaceViewType
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        enum class PlaceViewType {
            CHIPS_VIEW, VERTICAL_VIEW
        }
    }

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

    inner class PlaceTypeChipGroupViewHolder(private val binding: LayoutPlaceGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceTypeContainer) {
            binding.apply {
                tvPlaceType.text = type.type + " (+${type.placeTypes.size})"
                llPlaceType.setOnClickListener {
                    tvPlaceType.text = type.type + if(type.isOpened) "" else " (+${type.placeTypes.size})"
                }


                type.placeTypes.forEachIndexed { index, it ->
                    addChips(binding.chipGroupPlaces, it, index) { type ->
                        onItemClickListener?.invoke(type)
                    }
                }
            }
        }
    }

    inner class PlaceTypeVerticalGroupViewHolder(private val binding: LayoutPlaceGroupItemVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(type: PlaceTypeContainer) {
            val placeTypeAdapter = PlacesTypeAdapter(appContext)
            binding.apply {
                tvPlaceType.text = type.type + " (+${type.placeTypes.size})"
                rvPlaceTypes.apply {
                    adapter = placeTypeAdapter
                    layoutManager =
                        LinearLayoutManager(appContext, LinearLayoutManager.VERTICAL, false)
                }
                placeTypeAdapter.setPlacesType(types = type.placeTypes)
                placeTypeAdapter.setOnPlaceTypeSelected {type->
                    onItemClickListener?.invoke(type)
                }
            }
        }
    }

    fun rotateViewBy(view: View, toDegree: Float) {
        view.rotation = toDegree
    }

    private fun addChips(
        chipGroup: ChipGroup,
        type: PlaceType,
        index: Int,
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
        chipGroup.applyCheckedOnSelectedChip(index, type.isSelected)
    }

    private fun ChipGroup.applyCheckedOnSelectedChip(index: Int, isSelected: Boolean) {
        val chip: Chip = this.getChildAt(index) as Chip
        chip.isChecked = isSelected
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            PlaceViewType.CHIPS_VIEW.ordinal -> {
                PlaceTypeChipGroupViewHolder(
                    LayoutPlaceGroupItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            PlaceViewType.VERTICAL_VIEW.ordinal -> {
                PlaceTypeVerticalGroupViewHolder(
                    LayoutPlaceGroupItemVerticalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> PlaceTypeChipGroupViewHolder(
                LayoutPlaceGroupItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = _types.size

    override fun getItemViewType(position: Int): Int {
        return placeTypeView.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PlaceTypeChipGroupViewHolder) {
            holder.bindItem(_types[position])
        } else if (holder is PlaceTypeVerticalGroupViewHolder) {
            holder.bindItem(_types[position])
        }
    }
}