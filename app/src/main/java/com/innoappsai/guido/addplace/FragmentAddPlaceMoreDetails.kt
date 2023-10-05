package com.innoappsai.guido.addplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.ImageAdapter
import com.innoappsai.guido.adapters.PlaceFeaturesAdapter
import com.innoappsai.guido.adapters.PlaceTimingsAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.adapters.VideoAdapter
import com.innoappsai.guido.addplace.dialog.PlaceOpeningDateTimeDialog
import com.innoappsai.guido.addplace.dialog.PlaceOpeningDateTimeDialog.Companion.DateTimeType
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.addplace.viewModels.PlaceMoreDetailsViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceMoreDetailsBinding
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceMoreDetails :
    BaseFragment<FragmentAddPlaceMoreDetailsBinding>(FragmentAddPlaceMoreDetailsBinding::inflate) {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private val placeMoreDetailsViewModel: PlaceMoreDetailsViewModel by viewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter
    private lateinit var adapterPlaceFeatures: PlaceFeaturesAdapter
    private lateinit var adapterPlaceTimings: PlaceTimingsAdapter

    private lateinit var workManager: WorkManager

    private val daysList =
        arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    private val timeList = arrayListOf(
        "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM",
        "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM",
        "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
        "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM",
        "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM",
        "09:00 PM", "10:00 PM", "11:00 PM", "12:00 AM"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
        adapterPlaceFeatures = PlaceFeaturesAdapter(requireContext())
        adapterPlaceTimings = PlaceTimingsAdapter(requireContext())
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialogFragment = PlaceOpeningDateTimeDialog(requireContext())
        dialogFragment.setOnPlaceDateOrTimeSelected { value, type ->
            when (type) {
                DateTimeType.DAY -> {
                    placeMoreDetailsViewModel.onDayOfTheWeekSelected(value)
                }

                DateTimeType.FROM -> {
                    placeMoreDetailsViewModel.onWorkingHoursFrom(value)
                }

                DateTimeType.TO -> {
                    placeMoreDetailsViewModel.onWorkingHoursTo(value)
                }

                null -> Unit
            }
        }


        binding.apply {
            rvFeatures.apply {
                adapter = adapterPlaceFeatures
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            rvAddedBusinessHours.apply {
                adapter = adapterPlaceTimings
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            ivArrowBack.setOnClickListener { findNavController().popBackStack() }
            tiLayoutPlaceBusinessDays.setEndIconOnClickListener {
                dialogFragment.apply {
                    show()
                    setData("Choose You Day", DateTimeType.DAY, daysList)
                }
            }
            tiLayoutPlaceBusinessOpeningFrom.setEndIconOnClickListener {
                dialogFragment.apply {
                    show()
                    setData("Choose You Opening Hour", DateTimeType.FROM, timeList)
                }
            }
            tiLayoutPlaceBusinessOpeningTo.setEndIconOnClickListener {
                dialogFragment.apply {
                    show()
                    setData("Choose You CLosing Hour", DateTimeType.TO, timeList)
                }
            }
            btnAddOpeningTimings.setOnClickListener {
                val placeOpeningDay = etPlaceBusinessOpeningDays.text.toString()
                val placeOpeningHour = etPlaceBusinessOpeningFrom.text.toString()
                val placeClosingHour = etPlaceBusinessOpeningTo.text.toString()

                tiLayoutPlaceBusinessDays.error = null
                tiLayoutPlaceBusinessOpeningFrom.error = null
                tiLayoutPlaceBusinessOpeningTo.error = null

                if (placeOpeningDay.isNullOrEmpty()) {
                    tiLayoutPlaceBusinessDays.error = "Please select a day of the week"
                    return@setOnClickListener
                }

                if (placeOpeningHour.isNullOrEmpty()) {
                    tiLayoutPlaceBusinessOpeningFrom.error =
                        "Please select a when your business starts"
                    return@setOnClickListener
                }

                if (placeClosingHour.isNullOrEmpty()) {
                    tiLayoutPlaceBusinessOpeningTo.error =
                        "Please select a when your business closes"
                    return@setOnClickListener
                }

                placeMoreDetailsViewModel.onPlaceTimingAdded(
                    placeOpeningDay, placeOpeningHour, placeClosingHour
                )

            }


            tvNext.setOnClickListener {
                val placeWebsite = etPlaceWebsite.text.toString()
                val placeInstagram = etPlaceInstagramPage.text.toString()
                val placeFacebook = etPlaceFacebookPage.text.toString()
                val placeBusinessEmail = etPlaceBusinessEmail.text.toString()
                val placeOwner = etPlaceBusinessOwner.text.toString()
                val placeSpecialNotes = etPlaceBusinessNotes.text.toString()


                tiLayoutPlaceInstagramPage.error = null
                tiLayoutPlaceFacebookPage.error = null
                tiLayoutPlaceBusinessEmail.error = null
                tiLayoutPlaceBusinessOwner.error = null
                tiLayoutPlaceBusinessNotes.error = null


                val placeOpeningCloseTimeList =
                    placeMoreDetailsViewModel.getAllOpeningAndCloseTimings()
                val allPlaceFeatures = placeMoreDetailsViewModel.getAllPlaceFeatures()
                viewModel.setMoreDetails(
                    placeWebsite,
                    placeInstagram,
                    placeFacebook,
                    placeBusinessEmail,
                    placeOwner,
                    placeSpecialNotes,
                    placeOpeningCloseTimeList,
                    allPlaceFeatures
                )
            }
        }
        viewModel.apply {
            navigateNext.collectIn(viewLifecycleOwner) {
                findNavController().navigate(R.id.fragmentAddPlaceImageVideos)
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }

        }

        placeMoreDetailsViewModel.apply {
            placeFeatures.observe(viewLifecycleOwner) {
                adapterPlaceFeatures.setPlaceFeatures(it)
            }
            placeTimings.observe(viewLifecycleOwner) {
                adapterPlaceTimings.setPlaceFeatures(it)
            }
            selectedDayOfTheWeek.observe(viewLifecycleOwner) {
                binding.etPlaceBusinessOpeningDays.setText(it)
            }
            selectedWorkingHourFrom.observe(viewLifecycleOwner) {
                binding.etPlaceBusinessOpeningFrom.setText(it)
            }
            selectedWorkingHourTo.observe(viewLifecycleOwner) {
                binding.etPlaceBusinessOpeningTo.setText(it)
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
        }

        adapterPlaceFeatures.setOnPlaceFeatureSelected {
            placeMoreDetailsViewModel.onPlaceFeatureClicked(it)
        }
        adapterPlaceTimings.setOnPlaceTimingDelete {
            placeMoreDetailsViewModel.onPlaceTimingDeleted(it)
        }
    }


}