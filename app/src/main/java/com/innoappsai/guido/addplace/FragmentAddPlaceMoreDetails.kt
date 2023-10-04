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
    private lateinit var adapterImage: ImageAdapter
    private lateinit var adapterVideo: VideoAdapter
    private lateinit var adapterPlaceFeatures: PlaceFeaturesAdapter
    private lateinit var adapterPlaceTimings: PlaceTimingsAdapter

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
        adapterImage = ImageAdapter(requireContext())
        adapterVideo = VideoAdapter(requireContext())
        adapterPlaceFeatures = PlaceFeaturesAdapter(requireContext())
        adapterPlaceTimings = PlaceTimingsAdapter(requireContext())
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
            btnAddOpeningTimings.setOnClickListener {
                val placeOpeningDay = etPlaceBusinessOpeningDays.text.toString()
                val placeOpeningHour = etPlaceBusinessOpeningFrom.text.toString()
                val placeClosingHour = etPlaceBusinessOpeningTo.text.toString()

                tiLayoutPlaceBusinessHours.error = null
                tiLayoutPlaceBusinessOpeningFrom.error = null
                tiLayoutPlaceBusinessOpeningTo.error = null

                if (placeOpeningDay.isNullOrEmpty()) {
                    tiLayoutPlaceBusinessHours.error = "Please select a day of the week"
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
                    placeOpeningDay, placeClosingHour, placeClosingHour
                )

            }


            ivComplete.setOnClickListener {
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



                viewModel.setMoreDetails(
                    placeInstagram, placeFacebook, placeBusinessEmail, placeOwner, placeSpecialNotes
                )
            }
        }
        viewModel.apply {
            currentScreenName.collectIn(viewLifecycleOwner) {
                findNavController().navigate(R.id.fragmentAddPlaceImageVideos)
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

        placeMoreDetailsViewModel.apply {
            placeFeatures.observe(viewLifecycleOwner) {
                adapterPlaceFeatures.setPlaceFeatures(it)
            }
            placeTimings.observe(viewLifecycleOwner) {
                adapterPlaceTimings.setPlaceFeatures(it)
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