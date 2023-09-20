package com.guido.app.fragments


import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentUserDetailsBinding
import com.guido.app.db.AppPrefs
import com.guido.app.model.User
import com.guido.app.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserDetailsFragment :
    BaseFragment<FragmentUserDetailsBinding>(FragmentUserDetailsBinding::inflate) {


    private lateinit var viewModel: UserDetailsViewModel

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[UserDetailsViewModel::class.java]
        viewModel.isFromSignUpFlow = requireArguments().getBoolean("IS_FROM_AUTH_FLOW", false)
        val tempUser: User? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getParcelable(
                    "TEMP_USER",
                    User::class.java
                )
            } else {
                requireArguments().getParcelable("TEMP_USER")
            }
        viewModel.getTempUser(tempUser)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            getUserDetailsByUserId(appPrefs.userId)
            user.observe(viewLifecycleOwner) {
                binding.apply {
                    etProfileName.setText(it.displayName)
                    etProfileBaseLocation.setText(it.location)
                    etProfileBaseEmail.setText(it.email)
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
                viewModel.createUser()
            }
        }

    }


}