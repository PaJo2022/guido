package com.innoappsai.guido.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.adapters.PlacesGroupListAdapter
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.BottomsheetPalceOptionsBinding
import com.innoappsai.guido.databinding.BottomsheetPalceSortOptionsBinding
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomPlaceSortOptions : BottomSheetDialogFragment() {



    private lateinit var placesAdapter: PlacesGroupListAdapter

    private var _binding: BottomsheetPalceSortOptionsBinding? = null
    private val binding: BottomsheetPalceSortOptionsBinding get() = _binding!!


    private var onSuccessFullPlaceDeleted: (() -> Any?)? = null

    fun setOnSuccessFullPlaceDeleted(onSuccessFullPlaceDeleted: (() -> Any?)?) {
        this.onSuccessFullPlaceDeleted = onSuccessFullPlaceDeleted
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
        _binding = BottomsheetPalceSortOptionsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}