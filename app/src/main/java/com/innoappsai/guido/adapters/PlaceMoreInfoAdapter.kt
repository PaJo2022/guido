package com.innoappsai.guido.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.databinding.LayoutPlacesMoreInfoItemBinding
import com.innoappsai.guido.isURLValid
import com.innoappsai.guido.model.placesUiModel.ExtraInfoWithIcon

class PlaceMoreInfoAdapter(
) : RecyclerView.Adapter<PlaceMoreInfoAdapter.PlaceInfoViewHolder>() {


    private var _placeExtraInfoWithIcon: List<ExtraInfoWithIcon> = ArrayList()

    fun setPlaceExtraInfo(placeExtraInfoWithIcon: List<ExtraInfoWithIcon>) {
        _placeExtraInfoWithIcon = placeExtraInfoWithIcon
        notifyDataSetChanged()
    }

    private var onExtraInfoClicked: ((url: String) -> Any?)? = null

    fun setOnPaceExtraInfoClicked(onExtraInfoClicked: ((url: String) -> Any?)) {
        this.onExtraInfoClicked = onExtraInfoClicked
    }


    inner class PlaceInfoViewHolder(private val binding: LayoutPlacesMoreInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetJavaScriptEnabled")
        fun bindItem(placeWithExtraInfoWithIcon: ExtraInfoWithIcon) {
            binding.apply {
                placeWithExtraInfoWithIcon.icon?.let { ivInfoIcon.setImageResource(it) }
                tvInfoTitle.text = placeWithExtraInfoWithIcon.title
                tvMoreInfoTitle.text = placeWithExtraInfoWithIcon.extraInfo
                tvMoreInfoTitle.isVisible = !isURLValid(placeWithExtraInfoWithIcon.extraInfo.toString())
                ivNext.isVisible = isURLValid(placeWithExtraInfoWithIcon.extraInfo.toString())
                root.setOnClickListener {
                    if (isURLValid(placeWithExtraInfoWithIcon.extraInfo.toString())) {
                        onExtraInfoClicked?.invoke(placeWithExtraInfoWithIcon.extraInfo.toString())
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceInfoViewHolder(
        LayoutPlacesMoreInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun getItemCount() = _placeExtraInfoWithIcon.size

    override fun onBindViewHolder(holder: PlaceInfoViewHolder, position: Int) {
        holder.bindItem(_placeExtraInfoWithIcon[position])
    }
}