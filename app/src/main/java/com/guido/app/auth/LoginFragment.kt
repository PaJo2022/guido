package com.guido.app.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.identity.Identity.getSignInClient
import com.guido.app.BaseFragment
import com.guido.app.MainActivity
import com.guido.app.adapters.PlacesAutoCompleteAdapter
import com.guido.app.adapters.PlacesTypeGroupAdapter
import com.guido.app.adapters.VerticalGridCustomItemDecoration
import com.guido.app.auth.model.UserLoginState
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentLoginBinding
import com.guido.app.db.AppPrefs
import com.guido.app.fragments.SearchLocationViewModel
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
    private val searchViewModel: SearchLocationViewModel by viewModels()

    private lateinit var adapterPlaceAutoComplete: PlacesAutoCompleteAdapter
    private lateinit var placesTypeGroupAdapter: PlacesTypeGroupAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInMethods()
        createAccountMethods()

        binding.llCreateAccount.apply {
            rvPlaceSuggestions.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
            }
            rvInteretes.apply {
                addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                adapter = placesTypeGroupAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        viewModel.apply {
            userLoginState.observe(viewLifecycleOwner) {
                binding.apply {
                    llSignIn.root.isVisible = it is UserLoginState.UserRegisterAccount
                    llCreateAccount.root.isVisible = it is UserLoginState.UserCreateAccount
                }
                if (it is UserLoginState.UserSignedUp || it is UserLoginState.UserLoggedIn) {
                    requireActivity().finish()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            }
            isLoading.collectIn(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
            userInterestes.observe(viewLifecycleOwner) {
                placesTypeGroupAdapter.setPlacesType(it)
            }
        }
        searchViewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                adapterPlaceAutoComplete.setPredications(it)
            }

        }

        adapterPlaceAutoComplete.setOnPlaceSelected { placeAutocomplete ->
            searchViewModel.onPredictionSelected()
            adapterPlaceAutoComplete.setPredications(emptyList())
            binding.llCreateAccount.etLocation.setText(placeAutocomplete.area)
            binding.llCreateAccount.etLocation.requestFocus()
        }
        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            viewModel.onPlaceInterestClicked(it.id)
        }
    }

    private fun signInMethods() {
        binding.apply {
            llSignIn.btnSignIt.setOnClickListener {
                val email = llSignIn.etUserEmail.text.toString()
                val password = llSignIn.etUserPassword.text.toString()
                llSignIn.tiLayoutUserEmail.error = null
                llSignIn.tiLayoutUserPassword.error = null
                if (email.isEmpty() || !isEmailValid(email)) {
                    llSignIn.tiLayoutUserEmail.error = "Please enter email"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    llSignIn.tiLayoutUserPassword.error = "Please enter password"
                    return@setOnClickListener
                }
                viewModel.searchUserElseAddUser(email, password)

            }
            llSignIn.tvSignUp.setOnClickListener {
                (requireActivity() as AuthActivity).goToActivity(1)
            }
            llSignIn.llGoogleLogin.setOnClickListener {
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

    }

    private fun createAccountMethods() {
        binding.llCreateAccount.apply {
            etLocation.doOnTextChanged { text, start, before, count ->
                if (!text.isNullOrEmpty() && !searchViewModel.isPredictionSelected) {
                    searchViewModel.getPredictions(text.toString())
                }else{
                    searchViewModel.removePredictions()
                }
            }

            createAccountBtn.setOnClickListener {
                val userName = etUserName.text.toString()
                val userLocation = etLocation.text.toString()
                tiLayoutUserName.error = null
                tiLayoutUserLocation.error = null

                if (userName.isEmpty()) {
                    tiLayoutUserName.error = "Please enter your user name"
                    return@setOnClickListener
                }
                if (userLocation.isEmpty()) {
                    tiLayoutUserLocation.error = "Please select your location"
                    return@setOnClickListener
                }
                viewModel.createUser(userName, userLocation)
            }
        }
    }


}