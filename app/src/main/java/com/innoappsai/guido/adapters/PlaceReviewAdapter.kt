package com.innoappsai.guido.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

    private var _onItemClickListener: ((String) -> Any?)? = null

    fun setOnPhotoClicked(onItemClickListener : ((String) -> Any?)){
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceReviewViewHolder(private val binding: LayoutPlacesReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(reviewUiModel: Review) {
             Glide.with(appContext).load(reviewUiModel.user?.profilePicture).centerCrop().into(binding.ivAuthorImage)
              binding.tvAuthorName.text = reviewUiModel.user?.displayName
            binding.tvReviewStar.rating = reviewUiModel.rating?.toFloat() ?: 0.0f
            // binding.tvReviewDate.text = reviewUiModel.reviewDone
            binding.tvReviewTitle.text = reviewUiModel.description
            binding.tvReviewDescription.text = reviewUiModel.description
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