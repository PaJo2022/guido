package com.innoappsai.guido.fragments

import androidx.lifecycle.ViewModel
import com.innoappsai.guido.model.review.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewMediaListViewModel @Inject constructor() : ViewModel() {

    var review: Review? = null

}