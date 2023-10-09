package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.innoappsai.guido.databinding.LayoutAddImageItemSmallBinding
import com.innoappsai.guido.databinding.VideoItemLayoutSmallBinding
import com.innoappsai.guido.model.review.MediaType
import com.innoappsai.guido.model.review.PlaceMediaItem

class MediaAdapterSmall(private val appContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _mediaItems: List<PlaceMediaItem> = ArrayList()
    private val players: MutableList<ExoPlayer> = mutableListOf()


    private var _onPlaceMediaItemClickListener: ((position: Int) -> Any?)? = null

    fun setOnPlaceMediaItemClickListener(onPlaceMediaItemClickListener: ((position: Int) -> Any?)) {
        this._onPlaceMediaItemClickListener = onPlaceMediaItemClickListener
    }

    fun setPlaceMediaItems(mediaItems: List<PlaceMediaItem>) {
        _mediaItems = mediaItems
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(private val binding: VideoItemLayoutSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(position: Int, placeMediaItem: PlaceMediaItem) {
            val player = SimpleExoPlayer.Builder(appContext).build()
            val mediaItem = MediaItem.fromUri(placeMediaItem.url)
            player.setMediaItem(mediaItem)
            player.prepare()
            players.add(player)
            binding.playerView.player = player
            binding.root.setOnClickListener {
                _onPlaceMediaItemClickListener?.invoke(position)
            }
        }
    }

    inner class ImageViewHolder(private val binding: LayoutAddImageItemSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, placeMediaItem: PlaceMediaItem) {
            Glide.with(appContext).load(placeMediaItem.url).centerCrop().into(binding.ivImage)
            binding.root.setOnClickListener {
                _onPlaceMediaItemClickListener?.invoke(position)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            viewType == MediaType.Video.ordinal -> {
                VideoViewHolder(
                    VideoItemLayoutSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            viewType == MediaType.Image.ordinal -> {
                ImageViewHolder(
                    LayoutAddImageItemSmallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> VideoViewHolder(
                VideoItemLayoutSmallBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return _mediaItems[position].mediaType.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val videoItem = _mediaItems[position]
        if (holder is VideoViewHolder) {
            holder.bind(position, videoItem)
        } else if (holder is ImageViewHolder) {
            holder.bind(position, videoItem)
        }
    }

    override fun getItemCount(): Int {
        return _mediaItems.size
    }

    fun releasePlayers() {
        for (player in players) {
            player.release()
        }
        players.clear()
    }
}
