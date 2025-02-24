package com.innoappsai.guido.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutPlacesImagesSliderItemBinding
import com.innoappsai.guido.updateApiKey

class ImageSliderAdapter(private val appContext : Context) :
    RecyclerView.Adapter<ImageSliderAdapter.ViewPagerViewHolder>() {

    private var _photoList: List<String> = emptyList()

    fun setPlacePhotos(photoList: List<String>) {
        _photoList = photoList
        notifyDataSetChanged()
    }

    inner class ViewPagerViewHolder(val binding: LayoutPlacesImagesSliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(photo: String) {

            Glide.with(appContext)
                .load(updateApiKey(photo))
                .centerCrop()
                .placeholder(R.drawable.no_image_placeholder)
                .into(binding.ivPlaceImage)
        }

    }

    override fun getItemCount(): Int = _photoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {

        val binding = LayoutPlacesImagesSliderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(_photoList[position])
    }

}