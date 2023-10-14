package com.innoappsai.guido.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentPlaceItinearyBinding
import com.innoappsai.guido.toggleEnableAndAlpha
import com.innoappsai.guido.workers.CreateItineraryGeneratorWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPlaceItineary : BottomSheetDialogFragment() {


    private val viewModel: FragmentPlaceItinearyViewModel by viewModels()

    private var _binding: FragmentPlaceItinearyBinding? = null
    private val binding: FragmentPlaceItinearyBinding get() = _binding!!

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        _binding = FragmentPlaceItinearyBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message = arguments?.getString("GENERATE_ITINEARY_MESSAGE")
        val itineraryDbId = arguments?.getString("ITINERARY_DB_ID")
        binding.ivArrowBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (itineraryDbId != null) {
            viewModel.generatePlaceItineraryById(itineraryDbId)

        } else {
            viewModel.itineraryId?.let { viewModel.generatePlaceItineraryById(it) }
            message?.let {
                startGenerating(it)
            }
        }


        addOnBackPressedCallback {
            if (itineraryDbId != null) {
                parentFragmentManager.popBackStack()
            }else{
                parentFragmentManager.popBackStack()
                parentFragmentManager.popBackStack()
            }

        }

        viewModel.apply {
            generatedItineary.observe(viewLifecycleOwner) {
                binding.tvItinery.text = it
                binding.animationView.isVisible = false
                binding.tvLoading.isVisible = false
                binding.btnApply.toggleEnableAndAlpha(true)
            }
        }
    }

    private fun startGenerating(message: String) {
        val inputData = Data.Builder().putString(
            "ITINERARY_QUERY", message
        ).putString("ITINERARY_ID", viewModel.itineraryId).build()

        val uploadImageFileWorkRequest =
            OneTimeWorkRequestBuilder<CreateItineraryGeneratorWorker>().addTag(
                CreateItineraryGeneratorWorker.TAG
            ).setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()
        workManager.enqueueUniqueWork(
            "GENERATE_TRAVEL_ITINERARY",
            ExistingWorkPolicy.KEEP,
            uploadImageFileWorkRequest
        )

        workManager.getWorkInfosForUniqueWorkLiveData("GENERATE_TRAVEL_ITINERARY")
            .observe(viewLifecycleOwner) { works ->
                val workState = works.map { it.state }.firstOrNull()
                binding.animationView.isVisible =
                    workState != WorkInfo.State.SUCCEEDED && workState != WorkInfo.State.FAILED && workState != WorkInfo.State.CANCELLED
                binding.tvLoading.isVisible =
                    workState != WorkInfo.State.SUCCEEDED && workState != WorkInfo.State.FAILED && workState != WorkInfo.State.CANCELLED
                binding.btnApply.toggleEnableAndAlpha(workState == WorkInfo.State.SUCCEEDED || workState == WorkInfo.State.FAILED || workState == WorkInfo.State.CANCELLED)

            }

    }


}