package com.innoappsai.guido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(
    private val _bindingInflater : (inflater : LayoutInflater) -> VB
) : Fragment() {

    private var _binding: VB? = null
    val binding: VB get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = _bindingInflater.invoke(inflater)
        return binding.root
    }



}