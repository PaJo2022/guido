package com.guido.app.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate){

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            linearLayout.setOnClickListener {
                appPrefs.isUserLoggedIn = true
                findNavController().popBackStack()
                findNavController().navigate(R.id.locationSearchFragment)
            }
            button.setOnClickListener {
                appPrefs.isUserLoggedIn = true
                findNavController().popBackStack()
                findNavController().navigate(R.id.locationSearchFragment)
            }
            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.signupFragment)
            }
        }

        if(appPrefs.isUserLoggedIn){
            findNavController().popBackStack()
            findNavController().navigate(R.id.locationSearchFragment)
        }
    }

}