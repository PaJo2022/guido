package com.innoappsai.guido.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.adapters.PlacesAutoCompleteAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.CHIPS_VIEW
import com.innoappsai.guido.adapters.VerticalGridCustomItemDecoration
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.auth.model.UserLoginState
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentSignUpBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.fragments.SearchLocationViewModel
import com.innoappsai.guido.isEmailValid
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    @Inject
    lateinit var appPrefs: AppPrefs


    private val viewModel: SignUpViewModel by viewModels()
    private val searchViewModel: SearchLocationViewModel by viewModels()
    private lateinit var adapterPlaceAutoComplete: PlacesAutoCompleteAdapter
    private lateinit var placesTypeGroupAdapter: PlacesTypeGroupAdapter
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceAutoComplete = PlacesAutoCompleteAdapter(requireContext())
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(requireContext(), CHIPS_VIEW)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnBackPressedCallback {
            (requireActivity() as AuthActivity).goToActivity(0)
        }
        binding.apply {
            ivBack.setOnClickListener { (requireActivity() as AuthActivity).goToActivity(0) }
            swipeRefreshLayout.isEnabled = false
            llSignUp.btnLogin.setOnClickListener {
                (requireActivity() as AuthActivity).goToActivity(0)
            }
            llCreateAccount.rvPlaceSuggestions.apply {
                adapter = adapterPlaceAutoComplete
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL, false
                )
            }
            llCreateAccount.rvInteretes.apply {
                addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                adapter = placesTypeGroupAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        adapterPlaceAutoComplete.setOnPlaceSelected { placeAutocomplete ->
            searchViewModel.onPredictionSelected()
            adapterPlaceAutoComplete.setPredications(emptyList())
            binding.llCreateAccount.etLocation.setText(placeAutocomplete.area)
            binding.llCreateAccount.etLocation.requestFocus()
        }


        registerUserMethods()
        createAccountMethods()


        viewModel.apply {
            userInterestes.observe(viewLifecycleOwner) {
                placesTypeGroupAdapter.setPlacesType(it)
            }
            userLoginState.observe(viewLifecycleOwner) {
                binding.apply {
                    llSignUp.root.isVisible = it is UserLoginState.UserRegisterAccount
                    llCreateAccount.root.isVisible = it is UserLoginState.UserCreateAccount
                }
                if (it is UserLoginState.UserSignedUp) {
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
        }

        searchViewModel.apply {
            suggestedLocations.observe(viewLifecycleOwner) {
                adapterPlaceAutoComplete.setPredications(it)
            }
        }

        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            viewModel.onPlaceInterestClicked(it.id)
        }
    }

    private fun registerUserMethods() {
        binding.llSignUp.btnSignUp.setOnClickListener {
            val email = binding.llSignUp.etUserEmail.text.toString()
            val password = binding.llSignUp.etUserPassword.text.toString()
            binding.llSignUp.tiLayoutUserEmail.error = null
            binding.llSignUp.tiLayoutUserPassword.error = null

            if (email.isEmpty() || !isEmailValid(email)) {
                binding.llSignUp.tiLayoutUserEmail.error = "Please enter email"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.llSignUp.tiLayoutUserPassword.error = "Please enter password"
                return@setOnClickListener
            }
            signUpUser(email, password)

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

    private fun signUpUser(email: String, password: String) {
        viewModel.registerUser(email, password)
    }

    private fun createUser(userName: String, location: String) {
        viewModel.createUser(userName, location)
    }

}