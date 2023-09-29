package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.databinding.LayoutPlacesImagesItemBinding
import com.innoappsai.guido.databinding.LayoutPlacesStoryItemBinding
import com.innoappsai.guido.databinding.LayoutPlacesVideoItemBinding
import com.innoappsai.guido.model.PlaceDetailsUiModel
import com.innoappsai.guido.updateApiKey

class PlaceImageAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceImageAdapter.PlaceImageViewHolder>() {


    private var _placesPhotos: List<String> = ArrayList()

    fun setPlacePhotos(placesPhotos: List<String>) {
        _placesPhotos = placesPhotos
        notifyDataSetChanged()
    }

    private var _onItemClickListener: ((String) -> Any?)? = null

    fun setOnPhotoClicked(onItemClickListener : ((String) -> Any?)){
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceImageViewHolder(private val binding: LayoutPlacesImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(photo: String) {
            Glide.with(appContext).load(updateApiKey(photo)).centerCrop().into(binding.ivPlaceImage)
            binding.ivPlaceImage.setOnClickListener {
                _onItemClickListener?.invoke(photo)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceImageViewHolder(
        LayoutPlacesImagesItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )


    override fun getItemCount() = _placesPhotos.size

    override fun onBindViewHolder(holder: PlaceImageViewHolder, position: Int) {
        val data = _placesPhotos[position]

        holder.bindItem(data)
    }
}