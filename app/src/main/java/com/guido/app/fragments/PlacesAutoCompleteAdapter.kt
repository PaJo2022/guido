package com.guido.app.fragments


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




    fun setOnPlaceSelected(onPlaceSelected : ((placeId : String) -> Any?)){
        _onPlaceSelected = onPlaceSelected
    }

    private var _onPlaceSelected : ((placeId : String) -> Any?)? = null

    inner class PredictionHolder(private val binding: PlaceRecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(placeAutocomplete: PlaceAutocomplete) {
            binding.area.text = placeAutocomplete.area
            binding.address.text = placeAutocomplete.address
            binding.root.setOnClickListener {
                _onPlaceSelected?.invoke(placeAutocomplete.placeId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PredictionHolder(
        PlaceRecyclerItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
    )

    override fun getItemCount() = _predictedLocations.size

    override fun onBindViewHolder(holder: PredictionHolder, position: Int) {
        val location = _predictedLocations[position]
        holder.bind(location)
    }
}