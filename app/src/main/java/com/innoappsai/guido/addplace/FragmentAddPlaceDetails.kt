package com.innoappsai.guido.addplace

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceDetailsBinding
import com.innoappsai.guido.showToast
import com.innoappsai.guido.workers.AddPlaceWorker
import com.innoappsai.guido.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class FragmentAddPlaceDetails :
    BaseFragment<FragmentAddPlaceDetailsBinding>(FragmentAddPlaceDetailsBinding::inflate) {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter
    private lateinit var adapterImage: ImageAdapter
    private lateinit var adapterVideo: VideoAdapter

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
        adapterImage = ImageAdapter(requireContext())
        adapterVideo = VideoAdapter(requireContext())
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()

        binding.apply {
            tvPickImage.setOnClickListener {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            tvTakeImage.setOnClickListener {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            tvPickVideos.setOnClickListener {
                pickVideosFromGallery()
            }


            tvNext.setOnClickListener {
                val placeDescription = etPlaceDescription.text.toString()
                val placeContactNumber = etPlaceContactNumber.text.toString()
                val placeWebsite = etPlaceWebsite.text.toString()
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

                viewModel.setPlaceDetails(
                    placeDescription,
                    placeContactNumber,
                    placeWebsite,
                    placePriceRange
                )
            }
        }
        viewModel.apply {
            startAddingPlace.collectIn(viewLifecycleOwner) {
                startFetchingFeedData(it.first, it.second)
                requireActivity().showToast("Your Place Is Adding")
                requireActivity().finish()
            }
            currentScreenName.collectIn(viewLifecycleOwner) {
                if (it is AddPlaceViewModel.PlaceAddScreenName.COMPLETE) {
                    MyApp.placeRequestDTO = it.placeRequestDTO
                }
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
            placeImages.observe(viewLifecycleOwner) {
                adapterImage.setPlacePhotos(it)
            }
            placeVideos.observe(viewLifecycleOwner) {
                adapterVideo.setVideos(it)
            }
        }

    }


    private fun setUpViewPager() {
        binding.rvPlaceImages.adapter = adapterImage
        binding.rvPlaceImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        binding.rvPlaceVideos.adapter = adapterVideo
        binding.rvPlaceVideos.orientation = ViewPager2.ORIENTATION_HORIZONTAL


    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent ?: return@registerForActivityResult
            viewModel.addImageFilesToList(uriContent)
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


    private fun startFetchingFeedData(imageUri: Array<String>, videoUri: Array<String>) {
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
            OneTimeWorkRequestBuilder<UploadWorker>().addTag(UploadWorker.TAG).setInputData(imageFileFolder)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        val uploadVideoFileWorkRequest =
            OneTimeWorkRequestBuilder<UploadWorker>().setInputData(videoFileFolder)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        val addPlaceWorkRequest =
            OneTimeWorkRequestBuilder<AddPlaceWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        workManager
            .beginWith(listOf(uploadImageFileWorkRequest, uploadVideoFileWorkRequest))
            .then(addPlaceWorkRequest)
            .enqueue()


        // Observe the states of your work requests
        val uploadImageWorkInfoLiveData = workManager.getWorkInfoByIdLiveData(uploadImageFileWorkRequest.id)
        val uploadVideoWorkInfoLiveData = workManager.getWorkInfoByIdLiveData(uploadVideoFileWorkRequest.id)
        val addPlaceWorkInfoLiveData = workManager.getWorkInfoByIdLiveData(addPlaceWorkRequest.id)

// Create observers for each work request
        val uploadImageObserver = createWorkInfoObserver("UploadImageWorker", uploadImageFileWorkRequest.id)
        val uploadVideoObserver = createWorkInfoObserver("UploadVideoWorker", uploadVideoFileWorkRequest.id)
        val addPlaceObserver = createWorkInfoObserver("AddPlaceWorker", addPlaceWorkRequest.id)

// Observe the work request states
        uploadImageWorkInfoLiveData.observe(viewLifecycleOwner, uploadImageObserver)
        uploadVideoWorkInfoLiveData.observe(viewLifecycleOwner, uploadVideoObserver)
        addPlaceWorkInfoLiveData.observe(viewLifecycleOwner, addPlaceObserver)


    }

    private fun createWorkInfoObserver(workerName: String, requestId: UUID): Observer<WorkInfo> {
        return Observer { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> {
                    // Work request is enqueued
                    // You can perform actions when the work request is enqueued
                }

                WorkInfo.State.RUNNING -> {
                    // Work request is running
                    // You can perform actions when the work request is running
                }

                WorkInfo.State.SUCCEEDED -> {
                    // Work request completed successfully
                    // You can perform actions when the work request is completed successfully
                }

                WorkInfo.State.FAILED -> {
                    // Work request failed
                    // You can perform actions when the work request fails
                }

                else -> {
                    // Handle other states if needed
                }
            }
        }
    }

    private fun pickVideosFromGallery() {
        pickVideosLauncher.launch("video/*")
    }

    private val pickVideosLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {
                viewModel.addVideoUris(it)
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        adapterVideo.releasePlayers()
    }

}