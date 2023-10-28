package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelTimeDistributionBinding
import com.innoappsai.guido.databinding.LayoutTravelTimeSelectionBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelEachDayTiming : BaseFragment<LayoutItinearyGenerationTravelTimeDistributionBinding>(LayoutItinearyGenerationTravelTimeDistributionBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}