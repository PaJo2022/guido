package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelDurationBinding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.toggleEnableAndAlpha
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelDuration : BaseFragment<LayoutItinearyGenerationTravelDurationBinding>(LayoutItinearyGenerationTravelDurationBinding::inflate) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivIncreaseDays.setOnClickListener {
                viewModel.onDurationIncrease()
            }
            ivDecreaseDays.setOnClickListener {
                viewModel.onDurationDecrease()
            }
        }

        viewModel.apply {
            travelDurationState.observe(viewLifecycleOwner) {
                binding.ivIncreaseDays.toggleEnableAndAlpha(it < 10)
                binding.ivDecreaseDays.toggleEnableAndAlpha(it > 0)
                binding.tvDays.text = "${it} days"
            }
        }
    }
}