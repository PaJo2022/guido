package com.guido.app.fragments

import android.R
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.guido.app.BaseFragment
import com.guido.app.databinding.FragmentLocationVideoItemBinding
import com.guido.app.model.videosUiModel.VideoUiModel


class LandMarkVideoItemFragment(landMarkVideo: VideoUiModel) : BaseFragment<FragmentLocationVideoItemBinding>(FragmentLocationVideoItemBinding::inflate) {

    companion object {
        private const val LOCATION_VIDEO_DETAILS_ARG = "LOCATION_VIDEO_DETAILS_ARG"

        fun newInstance(landMarkVideo : VideoUiModel): LandMarkVideoItemFragment {
            val fragment = LandMarkVideoItemFragment(landMarkVideo)
            val args = Bundle()
            args.putParcelable(LOCATION_VIDEO_DETAILS_ARG,landMarkVideo)
            fragment.arguments = args
            return fragment
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customObject = arguments?.getParcelable<VideoUiModel>(LOCATION_VIDEO_DETAILS_ARG)

        binding.apply {
            tvVideoTitle.text = customObject?.title
            tvVideoDescription.text = customObject?.description
            webview.apply {
                val frameVideo =
                    "<html><body><br><iframe width=\"420\" height=\"315\" src=\"${customObject?.videoUrl}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"


                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return false
                    }
                }
                val webSettings = settings
                webSettings.javaScriptEnabled = true
                loadData(frameVideo, "text/html", "utf-8")
            }
        }

    }


}