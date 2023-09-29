package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.FragmentSearchLocationBinding


class LandMarkStoryItemFragment(landMarkName : String) : BaseFragment<FragmentSearchLocationBinding>(FragmentSearchLocationBinding::inflate) {

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


    }


}