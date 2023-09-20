package com.guido.app.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentSignUpBinding
import com.guido.app.db.AppPrefs
import com.guido.app.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs


    private val viewModel: SignUpViewModel by viewModels()

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.signUpBtn.setOnClickListener {
            appPrefs.isUserLoggedIn = true
            val email = binding.etUserEmail.text.toString()
            val password = binding.etUserPassword.text.toString()
            signUpUser(email, password)
        }

        viewModel.apply {
            userLoginState.collectIn(viewLifecycleOwner) {
                when (it) {
                    is UserLoginState.Error -> {
                        requireActivity().showToast(it.message)
                    }

                    is UserLoginState.Loading -> {

                    }


                    is UserLoginState.UserCreateAccount -> {
                        Bundle().apply {
                            putBoolean("IS_FROM_AUTH_FLOW", true)
                            putParcelable("TEMP_USER",it.user)
                            findNavController().navigate(R.id.userDetailsFragment, this)
                        }
                    }

                    else -> Unit
                }
            }
        }

    }

    private fun signUpUser(email: String, password: String) {
        viewModel.registerUser(email, password)
    }

}