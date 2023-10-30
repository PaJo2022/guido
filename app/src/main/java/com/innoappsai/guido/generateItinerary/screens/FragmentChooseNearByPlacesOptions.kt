package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.guido.cardstack.CardListener
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.R
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentChooseNearByPlacesBinding
import com.innoappsai.guido.generateItinerary.ViewModelChooseNearByPlaces
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.MainAdapter
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.workers.CreateItineraryGeneratorWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class FragmentChooseNearByPlacesOptions :
    BaseFragment<FragmentChooseNearByPlacesBinding>(FragmentChooseNearByPlacesBinding::inflate),
    CardListener {

    private val parentViewModel: ViewModelGenerateItinerary by activityViewModels()
    private val viewModel: ViewModelChooseNearByPlaces by viewModels()
    private lateinit var adapter: MainAdapter
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MainAdapter(requireActivity())
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardContainer.apply {
            setOnCardActionListener(this@FragmentChooseNearByPlacesOptions)
            setAdapter(adapter)
            maxStackSize = 3
//            setEmptyView(
//                LayoutInflater.from(requireContext()).inflate(R.layout.empty_layout, null, false)
//            )
//            addFooterView(
//                LayoutInflater.from(requireContext())
//                    .inflate(R.layout.example_footer_view, null, false)
//            )
//            addHeaderView(
//                LayoutInflater.from(requireContext())
//                    .inflate(R.layout.example_header_view, null, false)
//            )
        }
        viewModel.apply {
            fetchNearByPlace()
            nearByPlacesState.observe(viewLifecycleOwner) {

            }
        }
        parentViewModel.apply {
            fetchPlacesNearLocation()
            onItineraryGeneration.collectIn(viewLifecycleOwner) { message ->
                MyApp.itineraryGenerationMessage = message
                startGenerating(message)
            }
            selectedTypePlacesNearLocation.observe(viewLifecycleOwner) {
                adapter.setData(it)
            }
        }
    }

    private fun startGenerating(message: String) {
        val inputData = Data.Builder().putString("ITINERARY_ID", UUID.randomUUID().toString()).build()

        val createItineraryGenerationWork =
            OneTimeWorkRequestBuilder<CreateItineraryGeneratorWorker>().addTag(
                CreateItineraryGeneratorWorker.TAG
            ).setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()
        workManager.enqueueUniqueWork(
            "GENERATE_TRAVEL_ITINERARY",
            ExistingWorkPolicy.KEEP,
            createItineraryGenerationWork
        )
        requireActivity().finish()

//        workManager.getWorkInfosForUniqueWorkLiveData("GENERATE_TRAVEL_ITINERARY")
//            .observe(viewLifecycleOwner) { works ->
//                val workState = works.map { it.state }.firstOrNull()
//                binding.animationView.isVisible =
//                    workState != WorkInfo.State.SUCCEEDED && workState != WorkInfo.State.FAILED && workState != WorkInfo.State.CANCELLED
//                binding.tvLoading.isVisible =
//                    workState != WorkInfo.State.SUCCEEDED && workState != WorkInfo.State.FAILED && workState != WorkInfo.State.CANCELLED
//                binding.btnApply.toggleEnableAndAlpha(workState == WorkInfo.State.SUCCEEDED || workState == WorkInfo.State.FAILED || workState == WorkInfo.State.CANCELLED)
//
//            }

    }

    override fun onLeftSwipe(position: Int, model: Any) {
    }

    override fun onRightSwipe(position: Int, model: Any) {
        parentViewModel.cardRightSwiped(model as PlaceUiModel)
    }

    override fun onItemShow(position: Int, model: Any) {

    }

    override fun onSwipeCancel(position: Int, model: Any) {

    }

    override fun onSwipeCompleted() {
        Log.i("JAPAN", "onSwipeCompleted: ")
        parentViewModel.generateAiTextForItinerary()
    }
}