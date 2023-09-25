package com.guido.app.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity.getSignInClient
import com.guido.app.BaseFragment
import com.guido.app.MainActivity
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.db.AppPrefs
import com.guido.app.isEmailValid
import com.guido.app.showToast
import com.guido.app.sign_in.GoogleAuthUiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs

    private val viewModel: LoginViewModel by viewModels()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireActivity().applicationContext,
            oneTapClient = getSignInClient(requireActivity().applicationContext)
        )
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                lifecycleScope.launch {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                    } else {
                       // viewModel.setLoading(false)
                    }
                }
            }
        }
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
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
            llGoogleLogin.setOnClickListener {
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest
                            .Builder(
                                signInIntentSender ?: return@launch
                            )
                            .build()
                    )
                }
            }
        }

        viewModel.apply {
            userLoginState.observe(viewLifecycleOwner) {
                when (it) {
                    is UserLoginState.UserSignedUp -> {
                        requireActivity().finish()
                        startActivity(Intent(requireContext(),MainActivity::class.java))
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