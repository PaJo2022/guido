package com.guido.app.fragments

import android.os.Bundle
import android.view.View
import com.guido.app.BaseFragment
import com.guido.app.databinding.FragmentSearchLocationBinding
import com.guido.app.model.places.Photo


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