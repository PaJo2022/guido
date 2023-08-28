package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guido.app.databinding.LayoutPlacesImagesItemBinding
import com.guido.app.databinding.LayoutPlacesStoryItemBinding
import com.guido.app.databinding.LayoutPlacesVideoItemBinding
import com.guido.app.model.PlaceDetailsUiModel

class LocationDetailsAdapter(private val appContext : Context,private val placeDetailsType : PlaceDetailsType) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class PlaceDetailsType{
        IMAGE,STORY,VIDEOS
    }

    private var _placesDetails: List<PlaceDetailsUiModel> = ArrayList()

    fun setPlaceDetails(places: List<PlaceDetailsUiModel>) {
        _placesDetails = places
        notifyDataSetChanged()
    }

    inner class LocationImageViewHolder(private val binding: LayoutPlacesImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceDetailsUiModel) {
            binding.apply {

            }
        }
    }

    inner class LocationStoryViewHolder(private val binding: LayoutPlacesStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceDetailsUiModel) {
            binding.apply {

            }
        }
    }

    inner class LocationVideosViewHolder(private val binding: LayoutPlacesVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(place: PlaceDetailsUiModel) {
            binding.apply {
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder{
        return when(viewType){
            PlaceDetailsType.IMAGE.ordinal ->LocationImageViewHolder(
                LayoutPlacesImagesItemBinding.inflate(LayoutInflater.from(parent.context))
            )
            PlaceDetailsType.STORY.ordinal ->LocationStoryViewHolder(
                LayoutPlacesStoryItemBinding.inflate(LayoutInflater.from(parent.context))
            )
            PlaceDetailsType.VIDEOS.ordinal ->LocationVideosViewHolder(
                LayoutPlacesVideoItemBinding.inflate(LayoutInflater.from(parent.context))
            )
            else -> LocationStoryViewHolder(
                LayoutPlacesStoryItemBinding.inflate(LayoutInflater.from(parent.context))
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return placeDetailsType.ordinal
    }

    override fun getItemCount() = _placesDetails.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = _placesDetails[position]

        when(holder){
            is LocationImageViewHolder->{
                holder.bindItem(data)
            }
            is LocationStoryViewHolder->{
                holder.bindItem(data)
            }
            is LocationVideosViewHolder->{
                holder.bindItem(data)
            }
        }
    }
}