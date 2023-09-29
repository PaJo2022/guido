package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.FragmentSearchLocationBinding
import com.innoappsai.guido.model.places.Photo


class LandMarkImageItemFragment : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {

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



    }


}