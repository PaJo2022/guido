package com.innoappsai.guido.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutSortItemBinding
import com.innoappsai.guido.model.PlaceSortOption

class SortAdapter() : RecyclerView.Adapter<SortAdapter.SortItemViewHolder>() {


    private var _placeSortOptions: List<PlaceSortOption> = ArrayList()

    fun setSortOptions(placeSortOptions: List<PlaceSortOption>) {
        _placeSortOptions = placeSortOptions
        notifyDataSetChanged()
    }

    private var _onSortOptionSelected: ((String) -> Unit?)? = null

    fun setOnSortOptionSelected(onSortOptionSelected: ((String) -> Unit?)) {
        _onSortOptionSelected = onSortOptionSelected
    }

    inner class SortItemViewHolder(private val binding: LayoutSortItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeSortOption: PlaceSortOption) {
            binding.apply {
                tvSortItem.text = placeSortOption.name
                rbSelect.isChecked = placeSortOption.isChecked
                rbSelect.setOnClickListener {
                    _onSortOptionSelected?.invoke(placeSortOption.name)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SortItemViewHolder(
        LayoutSortItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeSortOptions.size

    override fun onBindViewHolder(holder: SortItemViewHolder, position: Int) {
        val data = _placeSortOptions[position]

        holder.bindItem(data)
    }
}