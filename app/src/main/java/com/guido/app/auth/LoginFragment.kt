package com.guido.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.guido.app.BaseFragment
import com.guido.app.MainActivity
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.db.AppPrefs
import com.guido.app.isEmailValid
import com.guido.app.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs

    private val viewModel: LoginViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            linearLayout.setOnClickListener {
                appPrefs.isUserLoggedIn = true

            }
            button.setOnClickListener {
                val email = binding.etUserEmail.text.toString()
                val password = binding.etUserPassword.text.toString()
                binding.tiLayoutUserEmail.error = null
                binding.tiLayoutUserPassword.error = null
                if (email.isEmpty() || !isEmailValid(email)) {
                    binding.tiLayoutUserEmail.error = "Please enter email"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    binding.tiLayoutUserPassword.error = "Please enter password"
                    return@setOnClickListener
                }
                viewModel.searchUserElseAddUser(email, password)

            }
            tvSignUp.setOnClickListener {
                (requireActivity() as AuthActivity).goToActivity(1)
            }
        }

        viewModel.apply {
            userLoginState.collectIn(viewLifecycleOwner) {
                when (it) {
                    is UserLoginState.Error -> requireActivity().showToast("User Is Not Signed Up")
                    is UserLoginState.Loading -> {}
                    is UserLoginState.UserCreateAccount -> {

                    }

                    is UserLoginState.UserLoggedIn -> {
                        requireActivity().finish()
                        startActivity(Intent(requireContext(),MainActivity::class.java))
                    }

                    else -> Unit
                }
            }
        }


    }



}