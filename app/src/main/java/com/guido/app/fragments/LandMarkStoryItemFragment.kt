package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.guido.app.BaseFragment
import com.guido.app.databinding.FragmentLocationImageItemBinding
import com.guido.app.model.places.Photo
import com.guido.app.model.placesUiModel.PlaceUiModel


class LandMarkStoryItemFragment(landMarkName : String) : BaseFragment<FragmentLocationImageItemBinding>(FragmentLocationImageItemBinding::inflate) {

    companion object {
        private const val LANDMARK_NAME = "LANDMARK_NAME"

        fun newInstance(landMarkName : String): LandMarkStoryItemFragment {
            val fragment = LandMarkStoryItemFragment(landMarkName)
            val args = Bundle()
            args.putString(LANDMARK_NAME,landMarkName)
            fragment.arguments = args
            return fragment
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val landMarkName = arguments?.getString(LANDMARK_NAME)



      //  https://maps.googleapis.com/maps/api/place/photo?photoreference=AUacShgil5gtPqLlM3S2GxbnVm5qj33LXHH7uPhoN8mZDj77in1kvrGJ0k-Ozs3KHy1hjzoxzw0t2ID6ifnif104D5_C3EOfaQxgXLGbrmWafTylOWekO1exetur73MYIndmeFzyBbm1DAT9SK7GNYPJ5nSlc_ZhDZEFnP1J40TN8FTrAWbR&sensor=false&maxheight=720&maxwidth=720&key=AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY

    }


}