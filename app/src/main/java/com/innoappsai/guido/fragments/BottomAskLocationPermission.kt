package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.adapters.PlacesGroupListAdapter
import com.innoappsai.guido.databinding.BottomsheetAskLocationPermissionBinding
import com.innoappsai.guido.openAppSettings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomAskLocationPermission : BottomSheetDialogFragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var placesAdapter: PlacesGroupListAdapter

    private var _binding: BottomsheetAskLocationPermissionBinding? = null
    private val binding: BottomsheetAskLocationPermissionBinding get() = _binding!!


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
        _binding = BottomsheetAskLocationPermissionBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placesAdapter = PlacesGroupListAdapter(requireContext())
        viewModel.shouldGoToSettings = requireArguments().getBoolean("SHOULD_GO_TO_SETTINGS", false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text =
            if (viewModel.shouldGoToSettings) "Please go the you app's settings and enable the location permission" else "We Need Your Location Access To Serve You The Best Tour Experience"
        binding.btnPermission.setOnClickListener {
            if (viewModel.shouldGoToSettings) {
                requireContext().openAppSettings()
            } else {
                viewModel.onLocationPermissionClicked()
            }
        }
    }

}