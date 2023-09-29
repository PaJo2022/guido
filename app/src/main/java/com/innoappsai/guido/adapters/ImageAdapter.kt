package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.databinding.LayoutAddImageItemBinding
import com.innoappsai.guido.updateApiKey

class ImageAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<ImageAdapter.PlaceImageFromFileViewHolder>() {


    private var _placesPhotos: List<ByteArray> = ArrayList()

    fun setPlacePhotos(placesPhotos: List<ByteArray>) {
        _placesPhotos = placesPhotos
        notifyDataSetChanged()
    }

    private var _onItemClickListener: ((String) -> Any?)? = null

    fun setOnPhotoClicked(onItemClickListener : ((String) -> Any?)){
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceImageFromFileViewHolder(private val binding: LayoutAddImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(photo: ByteArray) {
            Glide.with(appContext).load(photo).centerCrop().into(binding.ivImage)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceImageFromFileViewHolder(
        LayoutAddImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )


    override fun getItemCount() = _placesPhotos.size

    override fun onBindViewHolder(holder: PlaceImageFromFileViewHolder, position: Int) {
        val data = _placesPhotos[position]

        holder.bindItem(data)
    }
}