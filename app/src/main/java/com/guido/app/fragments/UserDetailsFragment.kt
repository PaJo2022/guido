package com.guido.app.fragments


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.guido.app.BaseFragment
import com.guido.app.MainActivity
import com.guido.app.R
import com.guido.app.auth.AuthActivity
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentUserDetailsBinding
import com.guido.app.db.AppPrefs
import com.guido.app.model.User
import com.guido.app.setNullText
import com.guido.app.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserDetailsFragment :
    BaseFragment<FragmentUserDetailsBinding>(FragmentUserDetailsBinding::inflate) {


    private lateinit var viewModel: UserDetailsViewModel
    private val auth by lazy { FirebaseAuth.getInstance() }

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        viewModel.isFromSignUpFlow = arguments?.getBoolean("IS_FROM_AUTH_FLOW", false) ?: false
        val tempUser: User? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(
                    "TEMP_USER",
                    User::class.java
                )
            } else {
                arguments?.getParcelable("TEMP_USER")
            }
        if (viewModel.isFromSignUpFlow) {
            viewModel.getTempUser(tempUser)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            if (!viewModel.isFromSignUpFlow) {
                getUserDetailsByUserId(appPrefs.userId)
            }
            user.observe(viewLifecycleOwner) {
                binding.apply {
                    etProfileName.setNullText(it?.displayName)
                    etProfileBaseLocation.setNullText(it?.location)
                    etProfileBaseEmail.setNullText(it?.email)
                }
            }
            userLoginState.collectIn(viewLifecycleOwner) {
                when (it) {
                    is UserLoginState.Error -> {
                        requireActivity().showToast(it.message)
                    }

                    is UserLoginState.Loading -> {

                    }

                    is UserLoginState.UserSignedUp -> {
                        findNavController().popBackStack(R.id.loginFragment, true)
                        findNavController().navigate(R.id.home_fragment)
                    }

                    else -> Unit
                }
            }
        }

        binding.apply {
            titlePageTitle.text =
                if (viewModel.isFromSignUpFlow) "Create Account" else "Edit Profile"
            btnCreate.isVisible = viewModel.isFromSignUpFlow
            btnLogout.isVisible = !viewModel.isFromSignUpFlow
            btnDeleteAccount.isVisible = !viewModel.isFromSignUpFlow
            btnCreate.setOnClickListener {
                val userName = binding.etProfileName.text.toString()
                val userLocation = binding.etProfileBaseLocation.text.toString()
                viewModel.createUser(userName, userLocation)
            }
            btnLogout.setOnClickListener {
                auth.signOut()
                viewModel.signOut()
                requireActivity().finish()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }
        }

    }


}