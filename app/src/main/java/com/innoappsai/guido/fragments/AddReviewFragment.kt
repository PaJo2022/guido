package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddReviewBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddReviewFragment :
    BaseFragment<FragmentAddReviewBinding>(FragmentAddReviewBinding::inflate) {

    private val viewModel: AddReviewViewModel by viewModels()


    @Inject
    lateinit var appPrefs: AppPrefs


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rating = arguments?.getFloat("PLACE_RATING") ?: 0.0f
        viewModel.placeId = arguments?.getString("PLACE_ID")
        binding.apply {
            rateBar.rating = rating
            tvRating.text = rating.toString()
            swipeRefreshLayout.isEnabled = false
            tvPost.setOnClickListener {
                val reviewTitle = etPlaceReviewTitle.text.toString()
                val reviewDescription = etPlaceReviewDescription.text.toString()
                val rating = rateBar.rating

                tiLayoutPlaceReviewTitle.error = null
                tiLayoutPlaceReviewDescription.error = null

                if (reviewTitle.isNullOrEmpty()) {
                    tiLayoutPlaceReviewTitle.error = "Please add a title for your review"
                    return@setOnClickListener
                }
                if (reviewDescription.isNullOrEmpty()) {
                    tiLayoutPlaceReviewDescription.error =
                        "Please add a description for your review"
                    return@setOnClickListener
                }

                viewModel.addNewTitle(reviewTitle, reviewDescription, rating)
            }
        }
        viewModel.apply {
            isLoading.collectIn(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
            isReviewAdded.collectIn(viewLifecycleOwner) {
                if (it) {
                    requireActivity().showToast("Your Review Is Added")
                } else {
                    requireActivity().showToast("Your Review Not Added")
                }
                parentFragmentManager.popBackStack()
            }
        }
    }


}