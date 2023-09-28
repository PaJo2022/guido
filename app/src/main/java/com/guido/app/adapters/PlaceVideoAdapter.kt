package com.guido.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.guido.app.databinding.LayoutPlacesVideoItemBinding

class PlaceVideoAdapter(
) : RecyclerView.Adapter<PlaceVideoAdapter.PlaceVideoViewHolder>() {


    private var _placeVideos: List<String?>? = ArrayList()

    fun setPlaceVideos(placeVideos: List<String?>?) {
        _placeVideos = placeVideos
        notifyDataSetChanged()
    }


    inner class PlaceVideoViewHolder(private val binding: LayoutPlacesVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(videoUrl: String) {
            val webSettings = binding.wvPlaceVideo.settings
            webSettings.javaScriptEnabled = true

            // Load the YouTube video URL
            binding.wvPlaceVideo.loadUrl(videoUrl)

            // Set a WebViewClient to handle redirects and other events
            binding.wvPlaceVideo.webViewClient = WebViewClient()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceVideoViewHolder(
        LayoutPlacesVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeVideos?.size ?: 0

    override fun onBindViewHolder(holder: PlaceVideoViewHolder, position: Int) {
        val videoUrl = _placeVideos?.getOrNull(position)

        videoUrl?.let { holder.bindItem(it) }
    }
}