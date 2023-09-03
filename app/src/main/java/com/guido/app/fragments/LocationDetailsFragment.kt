package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.adapters.TabAdapter
import com.guido.app.databinding.FragmentLocationDetailsBinding
import com.guido.app.model.placesUiModel.PlaceUiModel
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup

@AndroidEntryPoint
class LocationDetailsFragment :
    BaseFragment<FragmentLocationDetailsBinding>(FragmentLocationDetailsBinding::inflate) {

    private lateinit var viewModel: LandMarkDetailsViewModel
    private var vpLandmarkImages: TabAdapter? = null
    private var vpLandmarkStories: TabAdapter? = null
    private var vpLandmarkVideos: TabAdapter? = null

    private var videoFragmentList: List<Fragment> = emptyList()
    private var imageFragmentList: List<Fragment> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LandMarkDetailsViewModel::class.java]
        vpLandmarkImages = TabAdapter(childFragmentManager, lifecycle)
        vpLandmarkStories = TabAdapter(childFragmentManager, lifecycle)
        vpLandmarkVideos = TabAdapter(childFragmentManager, lifecycle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeUiModel = arguments?.getParcelable<PlaceUiModel>("LANDMARK_DATA")
        binding.apply {
            viewpagerLandmarkImages.adapter = vpLandmarkImages
            viewpagerLandmarkVideos.adapter = vpLandmarkVideos
        }
        viewModel.apply {
            setPlaceData(placeUiModel)
            placeUiModel?.name?.let {
                fetchAllDataForTheLocation(it)
            }
            landMarkData.observe(viewLifecycleOwner) {
                videoFragmentList = it?.locationVideos?.map { videoUiModel ->
                    LandMarkVideoItemFragment.newInstance(videoUiModel)
                } ?: emptyList()
                vpLandmarkVideos?.setFragmentsItems(videoFragmentList)

                imageFragmentList = it?.placeUiModel?.photos?.map { photo ->
                    LandMarkImageItemFragment.newInstance(photo)
                } ?: emptyList()
                vpLandmarkImages?.setFragmentsItems(imageFragmentList)
                binding.apply {
                    profileImage.setOnClickListener { findNavController().popBackStack() }
                    tvLandmarkName.text = it?.placeUiModel?.name
                    tvLandMarkLocation.text = it?.placeUiModel?.address

                }
            }
            landMarkTourDataData.observe(viewLifecycleOwner) {
                binding.tvTourGuide.apply {
                    settings.javaScriptEnabled = true
                    webViewClient = CustomWebViewClient()
                    webChromeClient = WebChromeClient()
                    // Parse the HTML string using Jsoup
                    val doc = Jsoup.parse(it)

                    // Extract the HTML code
                    val extractedHtml = doc.html()
                    val htmlContent = extractedHtml.trimIndent()

                    loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
                }
            }
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