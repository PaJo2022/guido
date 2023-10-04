package com.innoappsai.guido.addplace

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceImageVideosBinding
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.showToast
import com.innoappsai.guido.workers.AddPlaceWorker
import com.innoappsai.guido.workers.DownloadImageWorker
import com.innoappsai.guido.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceImageVideos :
    BaseFragment<FragmentAddPlaceImageVideosBinding>(FragmentAddPlaceImageVideosBinding::inflate) {

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
            ivArrowBack.setOnClickListener { findNavController().popBackStack() }
            tvPickImage.setOnClickListener {
                requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            tvTakeImage.setOnClickListener {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            tvPickVideos.setOnClickListener {
                requestGalleryPermissionForVideoLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }


            ivComplete.setOnClickListener {
                    viewModel.uploadPlaceData()
            }
        }
        viewModel.apply {
            startAddingPlace.collectIn(viewLifecycleOwner) {
                startFetchingFeedData(it.first, it.second)
                requireActivity().showToast("Your Place Is Adding")
                requireActivity().finish()
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


    fun generateStaticMapUrl(latitude: Double, longitude: Double): String {
        val apiKey =
            "AIzaSyBLXHjQ9_gyeSoRfndyiAz0lfvm-3fgpxY" // Replace with your Google Maps API key
        val marker = "icon:http://www.google.com/mapfiles/arrow.png|$latitude,$longitude"
        val size = "200x200" // Adjust the size as needed

        return "https://maps.googleapis.com/maps/api/staticmap?" +
                "size=$size&" +
                "markers=$marker&" +
                "key=$apiKey"
    }

    private fun startFetchingFeedData(imageUri: Array<String>, videoUri: Array<String>) {
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

}