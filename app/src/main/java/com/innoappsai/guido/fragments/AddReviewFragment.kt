package com.innoappsai.guido.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddReviewBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.showToast
import com.innoappsai.guido.workers.AddPlaceReviewWorker
import com.innoappsai.guido.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddReviewFragment :
    BaseFragment<FragmentAddReviewBinding>(FragmentAddReviewBinding::inflate) {

    private val viewModel: AddReviewViewModel by viewModels()
    private lateinit var workManager: WorkManager
    private lateinit var adapterImage: ImageAdapter
    private lateinit var adapterVideo: VideoAdapter

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterImage = ImageAdapter(requireContext())
        adapterVideo = VideoAdapter(requireContext())
        workManager = WorkManager.getInstance(requireContext())
    }

    private fun setUpViewPager() {
        binding.rvPlaceImages.adapter = adapterImage
        binding.rvPlaceImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL


        binding.rvPlaceVideos.adapter = adapterVideo
        binding.rvPlaceVideos.orientation = ViewPager2.ORIENTATION_HORIZONTAL


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var rating = arguments?.getFloat("PLACE_RATING") ?: 0.0f
        viewModel.placeId = arguments?.getString("PLACE_DB_ID")
        setUpViewPager()
        binding.apply {
            tvPickImage.setOnClickListener {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            tvTakeImage.setOnClickListener {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            tvPickVideos.setOnClickListener {
                requestGalleryPermissionForVideoLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            rateBar.rating = rating
            tvRating.text = rating.toString()
            swipeRefreshLayout.isEnabled = false
            rateBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
                binding.tvRating.text = fl.toString()
                rating = fl
            }
            tvPost.setOnClickListener {
                val reviewTitle = etPlaceReviewTitle.text.toString()
                val reviewDescription = etPlaceReviewDescription.text.toString()

                tiLayoutPlaceReviewTitle.error = null
                tiLayoutPlaceReviewDescription.error = null

                if (reviewTitle.isNullOrEmpty()) {
                    tiLayoutPlaceReviewTitle.error = "Please add a title for your review"
                    return@setOnClickListener
                }
                if (reviewDescription.isNullOrEmpty()) {
                    tiLayoutPlaceReviewDescription.error =
                        "Please add a description for your review"
                    return@setOnClickListener
                }

                viewModel.addNewTitle(reviewTitle, reviewDescription, rating)
            }
        }
        viewModel.apply {
            isLoading.collectIn(viewLifecycleOwner) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
            startAddReviewProcess.collectIn(viewLifecycleOwner) {
                startUploadingPlaceData(it.first, it.second)
                requireActivity().showToast("Your Review will be added")
                parentFragmentManager.popBackStack()
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
        addOnBackPressedCallback {
            parentFragmentManager.popBackStack()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        adapterVideo.releasePlayers()
    }

    private fun startUploadingPlaceData(imageUri: Array<String>, videoUri: Array<String>) {


        val imageFileFolder = Data.Builder()
            .putString(UploadWorker.OUTPUT_NAME, "IMAGE_FILES")
            .putStringArray(UploadWorker.FILE_URI, imageUri)
            .putString(UploadWorker.FOLDER_NAME, "places_review_images")
            .build()

        val videoFileFolder = Data.Builder()
            .putString(UploadWorker.OUTPUT_NAME, "VIDEO_FILES")
            .putStringArray(UploadWorker.FILE_URI, videoUri)
            .putString(UploadWorker.FOLDER_NAME, "places_review_videos")
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
        val addReviewRequest =
            OneTimeWorkRequestBuilder<AddPlaceReviewWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        workManager
            .beginWith(listOf(uploadImageFileWorkRequest, uploadVideoFileWorkRequest))
            .then(addReviewRequest)
            .enqueue()


    }


}