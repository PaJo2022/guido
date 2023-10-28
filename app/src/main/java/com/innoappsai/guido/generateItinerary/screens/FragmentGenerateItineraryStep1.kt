package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.CurrencyConstant
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentItinearyGenerationStep1Binding
import com.innoappsai.guido.databinding.FragmentPlaceItinearyGenerationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentGenerateItineraryStep1 :
    BaseFragment<FragmentItinearyGenerationStep1Binding>(FragmentItinearyGenerationStep1Binding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}