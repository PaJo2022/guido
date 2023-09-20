package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.Constants
import com.guido.app.databinding.LayoutPlacesImagesItemBinding
import com.guido.app.databinding.LayoutPlacesStoryItemBinding
import com.guido.app.databinding.LayoutPlacesVideoItemBinding
import com.guido.app.model.PlaceDetailsUiModel
import com.guido.app.model.places.Photo
import com.guido.app.model.placesUiModel.PlaceUiModel

class PlaceImageAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceImageAdapter.PlaceImageViewHolder>() {


    private var _placesPhotos: List<Photo> = ArrayList()

    fun setPlacePhotos(placesPhotos: List<Photo>) {
        _placesPhotos = placesPhotos
        notifyDataSetChanged()
    }

    private var _onItemClickListener: ((String) -> Any?)? = null

    fun setOnPhotoClicked(onItemClickListener : ((String) -> Any?)){
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceImageViewHolder(private val binding: LayoutPlacesImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(photo: Photo) {
            val photoUrl =
                "https://maps.googleapis.com/maps/api/place/photo?photoreference=${photo.photo_reference}&sensor=false&maxheight=${photo.height}&maxwidth=${photo.width}&key=${Constants.GCP_API_KEY}"

            Glide.with(appContext).load(photoUrl).centerCrop().into(binding.ivPlaceImage)
            binding.ivPlaceImage.setOnClickListener {
                _onItemClickListener?.invoke(photoUrl)
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceImageViewHolder(
        LayoutPlacesImagesItemBinding.inflate(LayoutInflater.from(parent.context))
    )


    override fun getItemCount() = _placesPhotos.size

    override fun onBindViewHolder(holder: PlaceImageViewHolder, position: Int) {
        val data = _placesPhotos[position]

        holder.bindItem(data)
    }
}