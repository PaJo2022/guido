package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.guido.app.BaseFragment
import com.guido.app.Constants
import com.guido.app.adapters.CustomItemDecoration
import com.guido.app.adapters.PlaceImageAdapter
import com.guido.app.adapters.PlaceReviewAdapter
import com.guido.app.databinding.FragmentLocationDetailsBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private lateinit var adapterPlaceImages: PlaceImageAdapter
    private lateinit var adapterPlaceReview: PlaceReviewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        adapterPlaceImages = PlaceImageAdapter(requireContext())
        adapterPlaceReview = PlaceReviewAdapter(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeUiModel = arguments?.getParcelable<PlaceUiModel>("LANDMARK_DATA")

        binding.apply {
            icArrowBack.setOnClickListener { findNavController().popBackStack() }
            rvPhotos.apply {
                addItemDecoration(CustomItemDecoration(requireContext()))
                adapter = adapterPlaceImages
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            }
            rvPlaceReviews.apply {
                adapter = adapterPlaceReview
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        viewModel.apply {
            getSinglePlaceDetails(placeUiModel)
            getDistanceBetweenMyPlaceAndTheCurrentPlace(placeUiModel)
            placeUiModel?.name?.let {
                fetchAllDataForTheLocation(it)
            }
            placeDistance.observe(viewLifecycleOwner) {
                binding.tvPlaceDistance.text = it

            }
            landMarkData.observe(viewLifecycleOwner) {


            }
            singlePlaceData.observe(viewLifecycleOwner) {
                binding.apply {
                    val customObject = it?.photos?.firstOrNull()
                    val photoUrl =
                        "https://maps.googleapis.com/maps/api/place/photo?photoreference=${customObject?.photo_reference}&sensor=false&maxheight=${customObject?.height}&maxwidth=${customObject?.width}&key=${Constants.GCP_API_KEY}"
                    Glide.with(requireContext()).load(photoUrl).centerCrop().into(ivPlaceImage)
                    tvPlaceName.text = it?.name
                    tvPlaceName.isSelected = true
                    tvPlaceAddress.text = it?.address
                    tvPlaceMobile.text = it?.callNumber ?: "No Contact Number"
                    tvPlaceWebsite.text = it?.website ?: "No Website"
                    llPlaceReviews.isVisible =  !it?.reviews.isNullOrEmpty()
                }
                it?.photos?.let { photos ->
                    adapterPlaceImages.setPlacePhotos(photos)
                }
                it?.reviews?.let {
                    adapterPlaceReview.setPlaceReviews(it)
                }

            }
            landMarkTourDataData.observe(viewLifecycleOwner) {

            }
        }

        adapterPlaceImages.setOnPhotoClicked {photoUrl->
            Glide.with(requireContext()).load(photoUrl).centerCrop().into(binding.ivPlaceImage)
        }

    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Set visibility to VISIBLE once the page is completely loaded
            view?.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TOUR_GUIDE =
            """To plan your visit to Nicco Park Kolkata and plot all the data as HTML code, you can follow these steps:\n\n1. Gather all the necessary information about Nicco Park Kolkata, including its address, timings, entry fees, attractions, rides, and facilities.\n\n2. Open a text editor or an HTML editor to write the HTML code.\n\n3. Start by creating the basic structure of the HTML document:\n\n```html\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Nicco Park Kolkata - Tour Guide</title>\n</head>\n<body>\n  <h1>Nicco Park Kolkata</h1>\n</body>\n</html>\n```\n\n4. Add the relevant information within the `<body>` section. This can include a description of the park, its location, and other details you wish to provide:\n\n```html\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Nicco Park Kolkata - Tour Guide</title>\n</head>\n<body>\n  <h1>Nicco Park Kolkata</h1>\n\n  <h2>About Nicco Park Kolkata</h2>\n  <p>Nicco Park is an amusement park located in Kolkata, West Bengal. It is one of the most popular amusement parks in the city, offering a wide range of attractions and rides for visitors of all ages.</p>\n\n  <h2>Location</h2>\n  <p>Address: [Insert address here]</p>\n  \n  <h2>Timings and Entry Fees</h2>\n  <p>Timings: [Insert timings here]</p>\n  <p>Entry Fees: [Insert entry fees here]</p>\n\n  <h2>Attractions and Rides</h2>\n  <ul>\n    <li>Attraction 1</li>\n    <li>Attraction 2</li>\n    <li>Attraction 3</li>\n    <!-- Add more attractions and rides as necessary -->\n  </ul>\n\n  <h2>Facilities</h2>\n  <ul>\n    <li>Facility 1</li>\n    <li>Facility 2</li>\n    <li>Facility 3</li>\n    <!-- Add more facilities as necessary -->\n  </ul>\n</body>\n</html>\n```\n\n5. Replace the placeholders with the actual data for address, timings, entry fees, attractions, and facilities.\n\n6. Save the file with an appropriate name, such as \"nicco-park-kolkata-tour-guide.html\".\n\n7. Now you can open the HTML file in a web browser to see how it appears with all the data inserted.\n\nRemember to properly format and style the HTML code using CSS if desired, to enhance the visual aesthetics of the tour guide."""

    }

}