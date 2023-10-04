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
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomPlaceOptions : BottomSheetDialogFragment() {


    private val viewModel: BottomPlaceOptionsViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val myPlaceViewModel: MyPlacesVideModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter

    private var _binding: BottomsheetPalceOptionsBinding? = null
    private val binding: BottomsheetPalceOptionsBinding get() = _binding!!


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
        _binding = BottomsheetPalceOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        viewModel.placeData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                "PLACE_DATA",
                PlaceUiModel::class.java
            )
        } else {
            arguments?.getParcelable("PLACE_DATA")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvPlaceName.text = viewModel.placeData?.name
            llDeletePlace.setOnClickListener {
                viewModel.deletePlaceById(viewModel.placeData?.placeId ?: return@setOnClickListener)
            }
        }

        viewModel.apply {
            onPlaceDeleted.collectIn(viewLifecycleOwner) {
                requireActivity().showToast("${it} is deleted")
                onSuccessFullPlaceDeleted?.invoke()
                myPlaceViewModel.removePlaceUsingPlaceId(
                    viewModel.placeData?.placeId ?: return@collectIn
                )
                homeViewModel.removePlaceUsingPlaceId(
                    viewModel.placeData?.placeId ?: return@collectIn
                )
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast("${it} is deleted")
            }
        }

    }

}