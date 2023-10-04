package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlaceFeatureBinding
import com.innoappsai.guido.model.PlaceFeature

class PlaceFeaturesAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceFeaturesAdapter.PlaceFeaturesViewHolder>() {


    private var _placeFeatures: List<PlaceFeature> = ArrayList()

    fun setPlaceFeatures(placeFeatures: List<PlaceFeature>) {
        _placeFeatures = placeFeatures
        notifyDataSetChanged()
    }

    private var _onPlaceFeatureItemClicked: ((String) -> Any?)? = null

    fun setOnPlaceFeatureSelected(onPlaceFeatureItemClicked: ((String) -> Any?)) {
        _onPlaceFeatureItemClicked = onPlaceFeatureItemClicked
    }

    inner class PlaceFeaturesViewHolder(private val binding: LayoutPlaceFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(placeFeature: PlaceFeature) {
            binding.apply {
                tvPlaceFeature.text = placeFeature.featureName
                cbPlaceFeature.isChecked = placeFeature.isSelected
                root.setOnClickListener {
                    _onPlaceFeatureItemClicked?.invoke(placeFeature.featureName)
                }
                cbPlaceFeature.setOnClickListener {
                    _onPlaceFeatureItemClicked?.invoke(placeFeature.featureName)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceFeaturesViewHolder(
        LayoutPlaceFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeFeatures.size

    override fun onBindViewHolder(holder: PlaceFeaturesViewHolder, position: Int) {
        val data = _placeFeatures[position]

        holder.bindItem(data)
    }
}