package com.guido.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.guido.app.Constants
import com.guido.app.databinding.LayoutPlacesImagesItemBinding
import com.guido.app.databinding.LayoutPlacesReviewItemBinding
import com.guido.app.databinding.LayoutPlacesStoryItemBinding
import com.guido.app.databinding.LayoutPlacesVideoItemBinding
import com.guido.app.formatMillisToDateString
import com.guido.app.model.PlaceDetailsUiModel
import com.guido.app.model.places.Photo
import com.guido.app.model.placesUiModel.PlaceUiModel
import com.guido.app.model.placesUiModel.ReviewUiModel

class PlaceReviewAdapter(
    private val appContext: Context
) : RecyclerView.Adapter<PlaceReviewAdapter.PlaceReviewViewHolder>() {


    private var _placeReview: List<ReviewUiModel> = ArrayList()

    fun setPlaceReviews(placeReview: List<ReviewUiModel>) {
        _placeReview = placeReview
        notifyDataSetChanged()
    }

    private var _onItemClickListener: ((String) -> Any?)? = null

    fun setOnPhotoClicked(onItemClickListener : ((String) -> Any?)){
        _onItemClickListener = onItemClickListener
    }

    inner class PlaceReviewViewHolder(private val binding: LayoutPlacesReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(reviewUiModel: ReviewUiModel) {
            Glide.with(appContext).load(reviewUiModel.authorProfilePic).centerCrop().into(binding.ivAuthorImage)
            binding.tvAuthorName.text = reviewUiModel.authorName
            binding.tvReviewStar.rating = reviewUiModel.authorRating.toFloat()
            binding.tvReviewDate.text = reviewUiModel.reviewDone
            binding.tvReview.apply {
                isVisible = reviewUiModel.reviewText.isNotEmpty()
                text = reviewUiModel.reviewText
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