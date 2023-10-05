package com.innoappsai.guido.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.innoappsai.guido.databinding.LayoutPlacesVideoItemBinding
import com.innoappsai.guido.databinding.VideoItemLayoutBinding
import com.innoappsai.guido.model.VideoItem
import com.innoappsai.guido.model.VideoType

class VideoAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var videoItems: ArrayList<VideoItem> = ArrayList()
    private val players: MutableList<ExoPlayer> = mutableListOf()


    private var onFullScreenButtonClick: ((videoUrl: String) -> Any?)? = null

    fun setOnFullScreenClickListener(onFullScreenButtonClick: ((videoUrl: String) -> Any?)) {
        this.onFullScreenButtonClick = onFullScreenButtonClick
    }

    fun setVideos(videos: ArrayList<VideoItem>) {
        videoItems = videos
        notifyDataSetChanged()
    }

    inner class UriVideoViewHolder(private val binding: VideoItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(videoItem: VideoItem) {
            val player = SimpleExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(videoItem.videoLink)
            player.setMediaItem(mediaItem)
            player.prepare()
            players.add(player)
            binding.playerView.player = player
        }
    }

    inner class YoutubeVideoViewHolder(private val binding: LayoutPlacesVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        fun bind(videoItem: VideoItem) {
            val webSettings = binding.wvPlaceVideo.settings
            webSettings.javaScriptEnabled = true
            webSettings.pluginState = WebSettings.PluginState.ON

            // Load the YouTube video URL
            binding.wvPlaceVideo.loadUrl(videoItem.videoLink)

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
                    onFullScreenButtonClick?.invoke(videoItem.videoLink)
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()

                }
            }

            binding.wvPlaceVideo.webChromeClient = webChromeClient
            binding.wvPlaceVideo.webViewClient = webViewClient
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            viewType == VideoType.OWN_VIDEO.ordinal -> {
                UriVideoViewHolder(
                    VideoItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == VideoType.YOUTUBE_VIDEO.ordinal -> {
                YoutubeVideoViewHolder(
                    LayoutPlacesVideoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> YoutubeVideoViewHolder(
                LayoutPlacesVideoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return videoItems[position].videoType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val videoItem = videoItems[position]
        if (holder is UriVideoViewHolder) {
            holder.bind(videoItem)
        } else if (holder is YoutubeVideoViewHolder) {
            holder.bind(videoItem)
        }
    }

    override fun getItemCount(): Int {
        return videoItems.size
    }

    fun releasePlayers() {
        for (player in players) {
            player.release()
        }
        players.clear()
    }
}
