package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelCompanionsBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelInterestsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelInterests : BaseFragment<LayoutItinearyGenerationTravelInterestsBinding>(LayoutItinearyGenerationTravelInterestsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}