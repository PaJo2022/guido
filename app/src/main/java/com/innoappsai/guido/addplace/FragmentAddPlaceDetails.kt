package com.innoappsai.guido.addplace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.MainActivity
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceDetailsBinding
import com.innoappsai.guido.generateStaticMapUrl
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.showToast
import com.innoappsai.guido.workers.AddPlaceWorker
import com.innoappsai.guido.workers.DownloadImageWorker
import com.innoappsai.guido.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceDetails :
    BaseFragment<FragmentAddPlaceDetailsBinding>(FragmentAddPlaceDetailsBinding::inflate) {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            ivArrowBack.setOnClickListener { findNavController().popBackStack() }
            tvGenerateDescription.setOnClickListener {
                viewModel.generatePlaceDescriptionUsingTheData()
            }


            tvNext.setOnClickListener {
                val placeDescription = etPlaceDescription.text.toString()
                val placeContactNumber = etPlaceContactNumber.text.toString()

                val selectedRadioButtonId = priceRangeBtnGroup.checkedRadioButtonId
                val selectedRadioButton =
                    priceRangeBtnGroup.findViewById<RadioButton>(selectedRadioButtonId)
                val placePriceRange = selectedRadioButton?.text.toString()

                tiLayoutPlaceDescription.error = null
                tiLayoutPlaceContactNumber.error = null

                if (placeDescription.length < 10) {
                    tiLayoutPlaceDescription.error = "Please enter Description for the Place"
                    return@setOnClickListener
                }
                if (placeContactNumber.isEmpty()) {
                    tiLayoutPlaceContactNumber.error = "Please enter contact number for the Place"
                    return@setOnClickListener
                }
                if (placePriceRange.isEmpty()) {
                    requireActivity().showToast("Please select the pricing for the Place")
                    return@setOnClickListener
                }

                viewModel.setPlaceBasicDetails(
                    placeDescription,
                    placeContactNumber,
                    placePriceRange
                )
            }
        }
        viewModel.apply {
            isLoading.collectIn(viewLifecycleOwner){
                (requireActivity() as AddPlaceActivity).toggleLoading(it)
            }
            isPlaceDescriptionGenerating.collectIn(viewLifecycleOwner){
                binding.etPlaceDescription.setText(it)
            }
            startAddingPlace.collectIn(viewLifecycleOwner) {
                startUploadingPlaceData(it.first, it.second)
                requireActivity().showToast("Your Place Is Adding")
                requireActivity().finish()
            }
            navigateNext.collectIn(viewLifecycleOwner) {
                findNavController().navigate(R.id.fragmentAddPlaceMoreDetails)
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
            generatedPlaceDescription.observe(viewLifecycleOwner){
                binding.etPlaceDescription.setText(it)
            }
        }

    }


    private fun startUploadingPlaceData(imageUri: Array<String>, videoUri: Array<String>) {
        val latitude = viewModel.getLatLong().first!!
        val longitude = viewModel.getLatLong().second!!
        val mapUrl = generateStaticMapUrl(latitude, longitude)
        val inputData = Data.Builder()
            .putString(
                DownloadImageWorker.KEY_IMAGE_URL,
                mapUrl
            )
            .putString(DownloadImageWorker.KEY_CACHE_FILE_NAME, "cached_image.jpg")
            .build()


        val downloadImageWorker =
            OneTimeWorkRequestBuilder<DownloadImageWorker>().setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()


        val imageFileFolder = Data.Builder()
            .putString(UploadWorker.OUTPUT_NAME, "IMAGE_FILES")
            .putStringArray(UploadWorker.FILE_URI, imageUri)
            .putString(UploadWorker.FOLDER_NAME, "places_images")
            .build()

        val videoFileFolder = Data.Builder()
            .putString(UploadWorker.OUTPUT_NAME, "VIDEO_FILES")
            .putStringArray(UploadWorker.FILE_URI, videoUri)
            .putString(UploadWorker.FOLDER_NAME, "places_videos")
            .build()


        val uploadImageFileWorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>().addTag(UploadWorker.TAG)
                .setInputData(imageFileFolder)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        val uploadVideoFileWorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>().setInputData(videoFileFolder)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        val uploadMapImageWorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        val addPlaceWorkRequest =
            OneTimeWorkRequestBuilder<AddPlaceWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        workManager
            .beginWith(downloadImageWorker)
            .then(uploadMapImageWorkRequest)
            .then(listOf(uploadImageFileWorkRequest, uploadVideoFileWorkRequest))
            .then(addPlaceWorkRequest)
            .enqueue()





    }



    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                cropImage.launch(
                    CropImageContractOptions(
                        null,
                        cropImageOptions = CropImageOptions(
                            imageSourceIncludeGallery = false,
                            cropShape = CropImageView.CropShape.OVAL
                        )
                    )
                )
            } else {
                requireContext().openAppSettings()
            }
        }



    private val requestGalleryPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                multiplePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                requireContext().openAppSettings()
            }
        }


    private val requestGalleryPermissionForVideoLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                multipleVideoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
            } else {
                requireContext().openAppSettings()
            }
        }





    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent ?: return@registerForActivityResult
            viewModel.addSingleImageFileToList(uriContent)
        } else {
            // An error occurred.
            val exception = result.error

        }
    }


    private val multiplePhotoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            if (it != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                it.forEach { uri ->
                    context?.contentResolver?.takePersistableUriPermission(uri, flag)
                }
                viewModel.addImageFilesToList(it)
            }
        }

    private val multipleVideoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            if (it != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                it.forEach { uri ->
                    context?.contentResolver?.takePersistableUriPermission(uri, flag)
                }
                viewModel.addVideoUris(it)
            }
        }



}