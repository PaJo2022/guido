package com.guido.app.fragments


import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.firebase.auth.FirebaseAuth
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.addOnBackPressedCallback
import com.guido.app.auth.AuthActivity
import com.guido.app.collectIn
import com.guido.app.databinding.FragmentUserDetailsBinding
import com.guido.app.db.AppPrefs
import com.guido.app.getImageBytes
import com.guido.app.model.User
import com.guido.app.setNullText
import com.guido.app.toggleEnableAndAlpha
import com.guido.app.toggleEnableAndVisibility
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
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
                    val icon = if (it == null) R.drawable.ic_add else R.drawable.ic_edit
                    binding.ivEditProfilePicture.setImageResource(icon)
                    Glide.with(requireContext()).load(it?.profilePicture).centerCrop()
                        .into(circleImageView)
                }
            }
            profilePicUrl.observe(viewLifecycleOwner) {
                val icon = if (it == null) R.drawable.ic_add else R.drawable.ic_edit
                binding.ivEditProfilePicture.setImageResource(icon)
                Glide.with(requireContext()).load(it).centerCrop().into(binding.circleImageView)
            }

            profileEditState.observe(viewLifecycleOwner) { editState ->
                binding.titleEditProfile.toggleEnableAndVisibility(editState == UserDetailsViewModel.ProfileEditingState.IDLE)
                binding.btnLogout.toggleEnableAndVisibility(editState == UserDetailsViewModel.ProfileEditingState.IDLE)
                binding.btnDeleteAccount.toggleEnableAndVisibility(editState == UserDetailsViewModel.ProfileEditingState.IDLE)
                binding.titleSaveProfile.toggleEnableAndVisibility(editState == UserDetailsViewModel.ProfileEditingState.EDITING)
                binding.ivCancelEdit.toggleEnableAndVisibility(editState == UserDetailsViewModel.ProfileEditingState.EDITING)
            }
            isProfilePicUpdating.collectIn(viewLifecycleOwner) { isUpdating ->
                binding.circularProgressProfilePic.isVisible = isUpdating
            }
            isProfileUpdating.collectIn(viewLifecycleOwner) { isUpdating ->
                binding.circularProgressProfile.isVisible = isUpdating
            }
        }

        binding.apply {
            btnCreate.isVisible = viewModel.isFromSignUpFlow
            btnLogout.isVisible = !viewModel.isFromSignUpFlow
            btnDeleteAccount.isVisible = !viewModel.isFromSignUpFlow
            btnCreate.setOnClickListener {
                val userName = binding.etProfileName.text.toString()
                val userLocation = binding.etProfileBaseLocation.text.toString()

            }
            icArrowBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            btnLogout.setOnClickListener {
                auth.signOut()
                viewModel.signOut()
                requireActivity().finish()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }
            ivEditProfilePicture.setOnClickListener {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            titleEditProfile.setOnClickListener {
                viewModel.onProfileEditClicked()
            }
            titleSaveProfile.setOnClickListener {

            }
            ivCancelEdit.setOnClickListener {
                viewModel.onProfileEditCanceled()
            }
        }


        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
    }


    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext())
            uriContent?.let { uri ->
                val file = File(uriFilePath)
                val fileArray = getImageBytes(file)
                viewModel.addFile(fileArray)
            }
        } else {
            // An error occurred.
            val exception = result.error

        }
    }


    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getImageFromCameraAndCrop()
            } else {

            }
        }

    private val requestGalleryPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getImageFromGalleryAndCrop()
            } else {

            }
        }

    private fun getImageFromCameraAndCrop() {
        cropImage.launch(
            CropImageContractOptions(
                null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeGallery = false,
                    cropShape = CropImageView.CropShape.OVAL
                )
            )
        )
    }

    private fun getImageFromGalleryAndCrop() {
        cropImage.launch(
            CropImageContractOptions(
                null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = false,
                    cropShape = CropImageView.CropShape.OVAL
                )
            )
        )
    }


}