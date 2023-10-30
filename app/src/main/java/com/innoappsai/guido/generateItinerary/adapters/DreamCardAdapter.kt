package com.innoappsai.guido.generateItinerary.adapters

import android.app.ActionBar
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.databinding.LayoutDreamCardBinding
import com.innoappsai.guido.getScreenHeight
import com.innoappsai.guido.getScreenWidth

class DreamCardAdapter(private val appContext: Context) :
    RecyclerView.Adapter<DreamCardAdapter.GridViewHolder>() {
    private var _items: List<Int> = emptyList()
    fun setDreamCardItems(items: List<Int>) {
        _items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        return GridViewHolder(
            LayoutDreamCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
       val imageSrc = _items[holder.layoutPosition%_items.size]
        holder.bind(holder.layoutPosition%_items.size,imageSrc)
    }

    override fun getItemCount() = Integer.MAX_VALUE

    inner class GridViewHolder(private val binding: LayoutDreamCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position : Int,imageSrc: Int) {
            val leftAndRightMargin =
                appContext.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
            val height1 =
                appContext.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._250sdp)
            val height2 =
                appContext.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
            val screenWidth = appContext.getScreenWidth() - (2*leftAndRightMargin)
            val screenHeight = if(position % 2 == 0) height1 else height2

            // Get screen width and height


            // Calculate new dimensions
            val newWidth = screenWidth / 3


            // Set ImageView dimensions
            val layoutParams = binding.parentLayout.layoutParams
            layoutParams.width = newWidth
            layoutParams.height = screenHeight
            binding.parentLayout.layoutParams = layoutParams
            Glide.with(appContext).load(imageSrc).centerCrop().into(binding.imageView)
        }
    }
}
