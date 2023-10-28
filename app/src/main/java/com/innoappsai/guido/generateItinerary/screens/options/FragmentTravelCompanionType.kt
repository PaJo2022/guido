package com.innoappsai.guido.generateItinerary.screens.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelBudgetBinding
import com.innoappsai.guido.databinding.LayoutItinearyGenerationTravelCompanionsBinding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.AdapterItemSelection
import com.innoappsai.guido.generateItinerary.adapters.itemdecorator.ItemHorizontalItemDecorator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentTravelCompanionType : BaseFragment<LayoutItinearyGenerationTravelCompanionsBinding>(
    LayoutItinearyGenerationTravelCompanionsBinding::inflate
) {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: AdapterItemSelection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = AdapterItemSelection(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvItems.apply {
                addItemDecoration(ItemHorizontalItemDecorator(requireContext()))
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = this@FragmentTravelCompanionType.adapter
            }
        }
        viewModel.apply {
            travelCompanionListState.observe(viewLifecycleOwner) {
                adapter.setInterestsOptions(it)
            }
        }
        adapter.setOnItemClickListener {
            viewModel.onTravelCompanionSelected(it)
        }
    }


}