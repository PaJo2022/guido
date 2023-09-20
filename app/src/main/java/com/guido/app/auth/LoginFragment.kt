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
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.db.AppPrefs
import com.guido.app.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs

    private val viewModel: LoginViewModel by viewModels()

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            linearLayout.setOnClickListener {
                appPrefs.isUserLoggedIn = true

            }
            button.setOnClickListener {
                val email = binding.etUserEmail.text.toString()
                val password = binding.etUserPassword.text.toString()
                viewModel.searchUserElseAddUser(email, password)

            }
            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.signUpFragment)
            }
        }

        viewModel.apply {
            userLoginState.collectIn(viewLifecycleOwner) {
                when (it) {
                    is UserLoginState.Error -> requireActivity().showToast("User Is Not Signed Up")
                    is UserLoginState.Loading -> {}
                    is UserLoginState.UserCreateAccount -> {
                        Bundle().apply {
                            putBoolean("IS_FROM_AUTH_FLOW", true)
                            putParcelable("TEMP_USER", it.user)
                            findNavController().navigate(R.id.userDetailsFragment, this)
                        }
                    }

                    is UserLoginState.UserLoggedIn -> {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.home_fragment)
                    }

                    else -> Unit
                }
            }
        }

//        if(appPrefs.isUserLoggedIn){

//        }
    }



}