package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDateBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelStartDate : BaseFragment<LayoutItinearyGenerationTravelDateBinding>(LayoutItinearyGenerationTravelDateBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}