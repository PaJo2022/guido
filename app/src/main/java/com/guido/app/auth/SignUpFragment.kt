package com.guido.app.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signUpBtn.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment,true)
            findNavController().navigate(R.id.locationSearchFragment)
        }

    }

}