package com.guido.app.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.guido.app.BaseFragment
import com.guido.app.MainActivity
import com.guido.app.adapters.PlacesAutoCompleteAdapter
import com.guido.app.auth.model.UserLoginState
import com.guido.app.databinding.FragmentSignUpBinding
import com.guido.app.db.AppPrefs
import com.guido.app.fragments.SearchLocationViewModel
import com.guido.app.isEmailValid
import com.guido.app.showToast
import com.guido.app.toggleEnableAndAlpha
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs


    private val viewModel: SignUpViewModel by viewModels()
    private val searchViewModel: SearchLocationViewModel by viewModels()
    private lateinit var adapterPlaceAutoComplete: PlacesAutoCompleteAdapter

    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener {
                (requireActivity() as AuthActivity).goToActivity(0)
            }
            llSignupInformation.rvPlaceSuggestions.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
            }
        }
        adapterPlaceAutoComplete.setOnPlaceSelected { placeAutocomplete->
            adapterPlaceAutoComplete.setPredications(emptyList())
            binding.llSignupInformation.etLocation.setText(placeAutocomplete.address)
        }
        binding.signUpBtn.setOnClickListener {
            appPrefs.isUserLoggedIn = true
            val email = binding.etUserEmail.text.toString()
            val password = binding.etUserPassword.text.toString()
            val userName = binding.llSignupInformation.etUserName.text.toString()
            val userLocation = binding.llSignupInformation.etLocation.text.toString()
            binding.tiLayoutUserEmail.error = null
            binding.tiLayoutUserPassword.error = null
            binding.llSignupInformation.tiLayoutUserName.error = null
            binding.llSignupInformation.tiLayoutUserLocation.error = null

            if (viewModel.userLoginState.value is UserLoginState.UserCreateAccount) {

                if (userName.isEmpty()) {
                    binding.llSignupInformation.tiLayoutUserName.error = "Please enter your user name"
                    return@setOnClickListener
                }
                if (userLocation.isEmpty()) {
                    binding.llSignupInformation.tiLayoutUserLocation.error = "Please enter lcoation"
                    return@setOnClickListener
                }
                createUser(userName, userLocation)
            } else

                if (email.isEmpty() || !isEmailValid(email)) {
                    binding.tiLayoutUserEmail.error = "Please enter email"
                    return@setOnClickListener
                }
            if (password.isEmpty()) {
                binding.tiLayoutUserPassword.error = "Please enter password"
                return@setOnClickListener
            }
            signUpUser(email, password)
        }


        binding.llSignupInformation.etLocation.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()) {
                searchViewModel.getPredictions(text.toString())
            }
        }


        viewModel.apply {
            userLoginState.observe(viewLifecycleOwner) {
                binding.apply {
                    etUserEmail.toggleEnableAndAlpha(it !is UserLoginState.UserCreateAccount)
                    etUserPassword.toggleEnableAndAlpha(it !is UserLoginState.UserCreateAccount)
                }
                binding.llSignupInformation.root.isVisible = it is UserLoginState.UserCreateAccount || it is UserLoginState.UserSignedUp
                binding.swipeRefreshLayout.isRefreshing = it is UserLoginState.Loading
                binding.signUpBtn.text =
                    if (it is UserLoginState.UserCreateAccount) "Lets Start" else "Sign Up"
                when (it) {
                    is UserLoginState.Error -> {
                        requireActivity().showToast(it.message)
                    }
                    is UserLoginState.UserCreateAccount -> {
                        binding.llSignupInformation.etUserName.requestFocus()
                    }

                    is UserLoginState.UserSignedUp -> {
                        requireActivity().finish()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }

                    else -> Unit
                }
            }
        }

        searchViewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                adapterPlaceAutoComplete.setPredications(it)
            }
        }

    }

    private fun signUpUser(email: String, password: String) {
        viewModel.registerUser(email, password)
    }

    private fun createUser(userName: String, location: String) {
        viewModel.createUser(userName, location)
    }

}