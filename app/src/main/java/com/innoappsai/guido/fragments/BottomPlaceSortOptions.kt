package com.innoappsai.guido.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.adapters.SortAdapter
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.BottomsheetPlaceSortOptionsBinding
import com.innoappsai.guido.model.SortType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomPlaceSortOptions : BottomSheetDialogFragment() {

    private lateinit var sortAdapter: SortAdapter
    private val viewModel: SortOptionViewModel by viewModels()

    private var _binding: BottomsheetPlaceSortOptionsBinding? = null
    private val binding: BottomsheetPlaceSortOptionsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sortAdapter = SortAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        _binding = BottomsheetPlaceSortOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            rvSortOptions.apply {
                adapter = sortAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            btnSaveChanges.setOnClickListener {
                viewModel.applySortOption()
            }
        }
        viewModel.apply {
            initAdapter()
            sortOptionsState.observe(viewLifecycleOwner) {
                sortAdapter.setSortOptions(it)
            }
            selectedSortOption.collectIn(viewLifecycleOwner) {
                _onSortOptionSelected?.invoke(it)
                dismiss()
            }
        }

        sortAdapter.setOnSortOptionSelected { name ->
            viewModel.onSortOptionSelected(name)
        }
    }

    private var _onSortOptionSelected: ((SortType) -> Unit?)? = null

    fun setOnSortOptionSelected(onSortOptionSelected: ((SortType) -> Unit?)) {
        _onSortOptionSelected = onSortOptionSelected
    }


}