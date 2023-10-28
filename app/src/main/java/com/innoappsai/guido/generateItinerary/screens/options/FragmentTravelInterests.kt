package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelInterestsBinding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.AdapterInterestsSelection
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelInterests : BaseFragment<LayoutItinearyGenerationTravelInterestsBinding>(LayoutItinearyGenerationTravelInterestsBinding::inflate) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: AdapterInterestsSelection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AdapterInterestsSelection(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvInterests.apply {
                adapter = this@FragmentTravelInterests.adapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }

        viewModel.apply {
            interestsSliderListState.observe(viewLifecycleOwner) {
                adapter.setInterestsOptions(it)
            }
        }

        adapter.setOnSliderChangeListener { id, value ->
            viewModel.onInterestsSliderMoved(id, value)
        }
    }
}