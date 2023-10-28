package com.innoappsai.guido.generateItinerary.adapters

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutItemBinding
import com.innoappsai.guido.generateItinerary.model.Item
import com.innoappsai.guido.getScreenHeight
import com.innoappsai.guido.getScreenWidth

class AdapterItemSelection(private val appContext: Context) :
    RecyclerView.Adapter<AdapterItemSelection.AdapterItemViewHolder>() {

    private var _options: List<Item> = emptyList()

    fun setInterestsOptions(options: List<Item>) {
        _options = options
        notifyDataSetChanged()
    }

    private var _onItemClick: ((id: String) -> Any?)? =
        null

    fun setOnItemClickListener(onItemClick: ((id: String) -> Any?)) {
        _onItemClick = onItemClick
    }

    inner class AdapterItemViewHolder(private val binding: LayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            val leftAndRightMargin =
                appContext.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._12sdp)
            val margin =
                appContext.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
            val screenWidth = appContext.getScreenWidth() - (2 * leftAndRightMargin) - (6 * margin)
            val screenHeight = appContext.getScreenHeight()

            // Get screen width and height


            // Calculate new dimensions
            val newWidth = screenWidth / _options.size


            // Set ImageView dimensions
            val layoutParams = binding.parentLayout.layoutParams
            layoutParams.width = newWidth
            layoutParams.height = LayoutParams.WRAP_CONTENT
            binding.parentLayout.layoutParams = layoutParams

            binding.apply {
                tvTitle.text = item.name
//                ivIcon.setImageResource(
//                    ContextCompat.getDrawable(
//                        appContext,
//
//                    )
//                )
                val colorStateList = if (item.isSelected) {
                    ContextCompat.getColorStateList(appContext, R.color.color_secondary)
                } else {
                    ContextCompat.getColorStateList(appContext, R.color.color_light_grey)
                }
                val textColor = if (item.isSelected) {
                    ContextCompat.getColor(appContext, R.color.white)
                } else {
                    ContextCompat.getColor(appContext, R.color.color_primary)
                }
                parentLayout.backgroundTintList = colorStateList
                tvTitle.setTextColor(textColor)
                parentLayout.setOnClickListener {
                    _onItemClick?.invoke(item.id)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterItemViewHolder {
        return AdapterItemViewHolder(
            LayoutItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = _options.size


    override fun onBindViewHolder(holder: AdapterItemViewHolder, position: Int) {
        val item = _options[position]
        holder.bind(item)
    }
}