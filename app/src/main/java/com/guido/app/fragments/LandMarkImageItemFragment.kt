package com.guido.app.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.guido.app.BaseFragment
import com.guido.app.databinding.FragmentLocationImageItemBinding
import com.guido.app.model.places.Photo
import com.guido.app.model.placesUiModel.PlaceUiModel


class LandMarkImageItemFragment : BaseFragment<FragmentLocationImageItemBinding>(FragmentLocationImageItemBinding::inflate) {

    companion object {
        private const val PLACE_DETAILS_ARG = "PLACE_DETAILS_ARG"

        fun newInstance(placePhoto : Photo): LandMarkImageItemFragment {
            val fragment = LandMarkImageItemFragment()
            val args = Bundle()
            args.putParcelable(PLACE_DETAILS_ARG,placePhoto)
            fragment.arguments = args
            return fragment
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customObject = arguments?.getParcelable<Photo>(PLACE_DETAILS_ARG)

        binding.apply {
            val photoUrl =
                "https://maps.googleapis.com/maps/api/place/photo?photoreference=${customObject?.photo_reference}&sensor=false&maxheight=${customObject?.height}&maxwidth=${customObject?.width}&key=AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY"
            Glide.with(requireContext()).load(photoUrl).centerCrop().into(imageView)
        }


    }


}