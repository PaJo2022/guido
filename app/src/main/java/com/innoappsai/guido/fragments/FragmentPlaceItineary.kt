package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.toggleEnableAndAlpha
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItineary : BottomSheetDialogFragment() {


    private val viewModel: FragmentPlaceItinearyViewModel by viewModels()

    private var _binding: FragmentPlaceItinearyBinding? = null
    private val binding: FragmentPlaceItinearyBinding get() = _binding!!


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
        _binding = FragmentPlaceItinearyBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message = arguments?.getString("GENERATE_ITINEARY_MESSAGE")
        binding.ivArrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        message?.let { viewModel.generatePlaceItineary(it) }

        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }

        viewModel.apply {
            generatedItineary.observe(viewLifecycleOwner) {
                binding.tvItinery.text = it
                binding.animationView.isVisible = false
                binding.tvLoading.isVisible = false
                binding.btnApply.toggleEnableAndAlpha(true)
            }
        }
    }


}