package com.guido.app.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            linearLayout.setOnClickListener {
                findNavController().popBackStack()
                findNavController().navigate(R.id.locationSearchFragment)
            }
            button.setOnClickListener {
                findNavController().popBackStack()
                findNavController().navigate(R.id.locationSearchFragment)
            }
            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.signupFragment)
            }
        }

    }

}