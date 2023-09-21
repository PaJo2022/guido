package com.guido.app.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guido.app.databinding.PlaceRecyclerItemLayoutBinding
import com.guido.app.model.PlaceAutocomplete


class PlacesAutoCompleteAdapter(private val mContext: Context) :
    RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder>() {

    private var _predictedLocations: List<PlaceAutocomplete> = ArrayList()

    fun setPredications(predictedLocations: List<PlaceAutocomplete>) {
        _predictedLocations = predictedLocations
        notifyDataSetChanged()
    }




    fun setOnPlaceSelected(onPlaceSelected : ((placeAutocomplete: PlaceAutocomplete) -> Any?)){
        _onPlaceSelected = onPlaceSelected
    }

    private var _onPlaceSelected : ((placeAutocomplete: PlaceAutocomplete) -> Any?)? = null

    inner class PredictionHolder(private val binding: PlaceRecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(placeAutocomplete: PlaceAutocomplete) {
            binding.address.text = placeAutocomplete.address + " , " + placeAutocomplete.area
            binding.root.setOnClickListener {
                _onPlaceSelected?.invoke(placeAutocomplete)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PredictionHolder(
        PlaceRecyclerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = _predictedLocations.size

    override fun onBindViewHolder(holder: PredictionHolder, position: Int) {
        val location = _predictedLocations[position]
        holder.bind(location)
    }
}