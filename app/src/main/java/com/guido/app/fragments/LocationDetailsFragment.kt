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
                    tvTourGuide.apply {
                        settings.javaScriptEnabled = true
                        webViewClient = CustomWebViewClient()
                        webChromeClient = WebChromeClient()

                        val htmlContent = TOUR_GUIDE.trimIndent()

                        loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
                    }
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
        const val TOUR_GUIDE = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nicco Park Visit Guide</title>
</head>
<body>
    <h1>Welcome to Nicco Park, Kolkata!</h1>
    <p>Nicco Park is a popular amusement park located in Salt Lake City, Kolkata. Here's how you can plan your visit:</p>
    
    <h2>Getting There:</h2>
    <ul>
        <li><strong>By Metro:</strong> The nearest metro station is "Salt Lake Stadium." From there, you can take a taxi or auto-rickshaw to reach the park.</li>
        <li><strong>By Bus:</strong> Buses to Salt Lake Sector V or Nicco Park are available.</li>
        <li><strong>By Taxi or Auto-rickshaw:</strong> Taxis and auto-rickshaws are readily available in the city.</li>
    </ul>
    
    <h2>Tickets and Entry:</h2>
    <p>Upon arrival, buy entry tickets. Prices vary based on the type of attractions you want to enjoy.</p>
    
    <h2>Exploring the Park:</h2>
    <p>Nicco Park offers a range of rides and attractions, including the Water Chute, Cyclone, and more. Plan your day with the park map.</p>
    
    <h2>Food and Refreshments:</h2>
    <p>Enjoy a variety of cuisines at food stalls and restaurants within the park.</p>
    
    <h2>Safety and Comfort:</h2>
    <p>Follow safety guidelines, wear comfortable clothing and footwear, and stay hydrated.</p>
    
    <h2>Closing Time:</h2>
    <p>Be aware of the park's closing time and plan your departure accordingly.</p>
    
    <p>Have a fantastic time at Nicco Park!</p>
</body>
</html>"""

    }

}