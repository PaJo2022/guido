package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.databinding.BottomsheetTwoButtonBinding


class BottomTwoButton(
    private val title: String,
    private val description: String,
    private val onPositiveButtonClick: () -> Unit,
    private val onNegetiveButtonClick: () -> Unit,
) : BottomSheetDialogFragment() {


    private var _binding: BottomsheetTwoButtonBinding? = null
    private val binding: BottomsheetTwoButtonBinding get() = _binding!!


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
        _binding = BottomsheetTwoButtonBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvTitle.text = title
            tvDescription.text = description
            btnPositive.setOnClickListener {
                onPositiveButtonClick.invoke()
            }
            btnNegetive.setOnClickListener {
                onNegetiveButtonClick.invoke()
            }
        }
    }

}