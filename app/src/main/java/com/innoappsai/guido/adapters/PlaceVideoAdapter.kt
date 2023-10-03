package com.innoappsai.guido.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlacesVideoItemBinding

class PlaceVideoAdapter(
) : RecyclerView.Adapter<PlaceVideoAdapter.PlaceVideoViewHolder>() {


    private var _placeVideos: List<String?>? = ArrayList()

    fun setPlaceVideos(placeVideos: List<String?>?) {
        _placeVideos = placeVideos
        notifyDataSetChanged()
    }

    private var onFullScreenButtonClick: ((videoUrl : String) -> Any?)? = null

    fun setOnFullScreenClickListener(onFullScreenButtonClick: ((videoUrl : String) -> Any?)) {
        this.onFullScreenButtonClick = onFullScreenButtonClick
    }


    inner class PlaceVideoViewHolder(private val binding: LayoutPlacesVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        fun bindItem(videoUrl: String) {
            val webSettings = binding.wvPlaceVideo.settings
            webSettings.javaScriptEnabled = true
            webSettings.pluginState = WebSettings.PluginState.ON

            // Load the YouTube video URL
            binding.wvPlaceVideo.loadUrl(videoUrl)

            // Set a WebViewClient to handle redirects and other events
            val webViewClient = object : WebViewClient() {
                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            val webChromeClient = object : WebChromeClient() {
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    onFullScreenButtonClick?.invoke(videoUrl)
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()

                }
            }

            binding.wvPlaceVideo.webChromeClient = webChromeClient
            binding.wvPlaceVideo.webViewClient = webViewClient
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