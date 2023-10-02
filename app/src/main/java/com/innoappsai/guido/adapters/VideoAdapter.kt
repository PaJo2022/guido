package com.innoappsai.guido.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.innoappsai.guido.R

class VideoAdapter(private val context: Context) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var videoItems: ArrayList<Uri> = ArrayList()
    private val players: MutableList<ExoPlayer> = mutableListOf()

    fun setVideos(videos: ArrayList<Uri>) {
        videoItems = videos
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerView: PlayerView = itemView.findViewById(R.id.playerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.video_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoItem = videoItems[position]
        val player = SimpleExoPlayer.Builder(context).build()
        holder.playerView.player = player
        val mediaItem = MediaItem.fromUri(videoItem)
        player.setMediaItem(mediaItem)
        player.prepare()
        players.add(player)
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
