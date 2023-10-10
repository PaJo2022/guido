package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.LayoutPlacesReviewItemBinding
import com.innoappsai.guido.model.review.Review

class PlaceReviewAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceReviewAdapter.PlaceReviewViewHolder>() {


    private var _placeReview: List<Review> = ArrayList()

    fun setPlaceReviews(placeReview: List<Review>) {
        _placeReview = placeReview
        notifyDataSetChanged()
    }

    private var _onItemClickListener: ((review: Review) -> Any?)? = null

    fun setOnReviewImageClicked(onItemClickListener: ((review: Review) -> Unit)) {
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceReviewViewHolder(private val binding: LayoutPlacesReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(reviewUiModel: Review) {
            val adapterMedia = MediaAdapterSmall(appContext)


            Glide.with(appContext).load(reviewUiModel.user?.profilePicture).centerCrop()
                .placeholder(R.drawable.ic_profile_img_placeholder)
                .error(R.drawable.ic_profile_img_placeholder).into(binding.ivAuthorImage)
            binding.tvAuthorName.text = reviewUiModel.user?.displayName
            binding.tvReviewStar.rating = reviewUiModel.rating?.toFloat() ?: 0.0f
            binding.tvReviewDate.isVisible = false
            binding.tvReviewTitle.text = reviewUiModel.description
            binding.tvReviewDescription.text = reviewUiModel.description
            binding.rvPlaceReviewImagesVideos.apply {
                adapter = adapterMedia
                layoutManager =
                    LinearLayoutManager(appContext, LinearLayoutManager.HORIZONTAL, false)
            }
            adapterMedia.setPlaceMediaItems(reviewUiModel.mediaFiles ?: emptyList())
            adapterMedia.setOnPlaceMediaItemClickListener {
                _onItemClickListener?.invoke(reviewUiModel)
            }
            binding.ivAuthorImage.setOnClickListener {
                // _onItemClickListener?.invoke(photoUrl)
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaceReviewViewHolder(
        LayoutPlacesReviewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )


    override fun getItemCount() = _placeReview.size

    override fun onBindViewHolder(holder: PlaceReviewViewHolder, position: Int) {
        val data = _placeReview[position]

        holder.bindItem(data)
    }
}