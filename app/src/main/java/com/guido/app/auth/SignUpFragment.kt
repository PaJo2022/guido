package com.guido.app.auth

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.databinding.FragmentSignUpBinding
import com.guido.app.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate){

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        binding.signUpBtn.setOnClickListener {
//            appPrefs.isUserLoggedIn = true
//            findNavController().popBackStack(R.id.loginFragment,true)
//            findNavController().navigate(R.id.locationSearchFragment)
//        }

    }

}