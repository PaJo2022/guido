package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
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
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.ExampleHeaderViewBinding
import com.innoappsai.guido.databinding.FragmentChooseNearByPlacesBinding
import com.innoappsai.guido.databinding.ItineraryGeneratingViewBinding
import com.innoappsai.guido.generateItinerary.ViewModelChooseNearByPlaces
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.MainAdapter
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
import com.innoappsai.guido.toggleEnableAndAlpha
import com.innoappsai.guido.workers.CreateItineraryGeneratorWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentChooseNearByPlacesOptions :
    BaseFragment<FragmentChooseNearByPlacesBinding>(FragmentChooseNearByPlacesBinding::inflate),
    CardListener {

    private val parentViewModel: ViewModelGenerateItinerary by activityViewModels()
    private val viewModel: ViewModelChooseNearByPlaces by viewModels()
    private lateinit var adapter: MainAdapter
    private lateinit var workManager: WorkManager
    private lateinit var headerBinding: ExampleHeaderViewBinding
    private lateinit var emptyLayoutBinding: ItineraryGeneratingViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MainAdapter(requireActivity())
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerBinding =
            ExampleHeaderViewBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        emptyLayoutBinding = ItineraryGeneratingViewBinding.inflate(
            LayoutInflater.from(requireContext()),
            null,
            false
        )
        binding.cardContainer.apply {
            setOnCardActionListener(this@FragmentChooseNearByPlacesOptions)
            setAdapter(adapter)
            maxStackSize = 3
            addHeaderView(
                headerBinding.root
            )
        }
        headerBinding.apply {
            tvDone.setOnClickListener {
                parentViewModel.generateAiTextForItinerary()
            }
        }
        viewModel.apply {
            fetchNearByPlace(parentViewModel.getSelectedPlaceTypes())
            selectedTypePlacesNearLocation.observe(viewLifecycleOwner) {
                adapter.setData(it)
            }
        }
        parentViewModel.apply {
            onItineraryGeneration.collectIn(viewLifecycleOwner) { message ->
                parentViewModel.startGeneratingItinerary(message)
                binding.apply {
                    cardContainer.isVisible = false
                    llItineraryGeneratingUi.root.isVisible = true
                    llItineraryGeneratingUi.appCompatButton.setOnClickListener {
                        requireActivity().finish()
                    }
                }
            }
            onNearByPlaceSelected.observe(viewLifecycleOwner) {
                val totalPlaceCanBedSelected = 15
                val placeSelected = it.size
                val progressPercentage = (placeSelected.toFloat() / totalPlaceCanBedSelected) * 100
                headerBinding.apply {
                    tvDone.toggleEnableAndAlpha(it.size > 6)
                    linearProgressIndicator.setProgress(progressPercentage.toInt(), true)
                    tvCompletion.text = "${progressPercentage.toInt()}%"
                }
                if(it.size == totalPlaceCanBedSelected ){
                    generateAiTextForItinerary()
                }
            }
        }
    }

    private fun startGenerating() {
        val inputData = Data.Builder().build()

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
        binding.apply {
            cardContainer.isVisible = false
            llItineraryGeneratingUi.root.isVisible = true
            llItineraryGeneratingUi.appCompatButton.setOnClickListener {
                requireActivity().finish()
            }
        }
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
        parentViewModel.generateAiTextForItinerary()
    }
}