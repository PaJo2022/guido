package com.innoappsai.guido.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.MediaAdapterLarge
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentReviewMediaFileListBinding
import com.innoappsai.guido.model.review.Review
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReviewMediaListFragment :
    BaseFragment<FragmentReviewMediaFileListBinding>(FragmentReviewMediaFileListBinding::inflate) {

    private lateinit var adapterMediaItems: MediaAdapterLarge
    private val viewModel: ReviewMediaListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterMediaItems = MediaAdapterLarge(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        viewModel.review = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                "PLACE_REVIEW_DATA",
                Review::class.java
            )
        } else {
            arguments?.getParcelable("PLACE_REVIEW_DATA")
        }
        viewModel.review?.let {
            binding.apply {
                Glide.with(requireContext()).load(it.user?.profilePicture).centerCrop()
                    .placeholder(R.drawable.ic_profile_img_placeholder)
                    .error(R.drawable.ic_profile_img_placeholder).into(ivAuthorImage)
                tvAuthorName.text = it.user?.displayName
                tvReviewStar.rating = it.rating?.toFloat() ?: 0.0f
                tvReviewTitle.text = it.title
            }
            adapterMediaItems.setPlaceMediaItems(it.mediaFiles ?: emptyList())
        }
        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
        binding.ivArrowBack.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun setUpViewPager() {
        binding.vpReviews.adapter = adapterMediaItems
        binding.vpReviews.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpReviews.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val totalItems = viewModel.review?.mediaFiles?.size ?: 0
                binding.toolbarTitle.text = "${position + 1} of ${totalItems}"
            }
        })


    }

}