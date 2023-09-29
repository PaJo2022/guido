package com.innoappsai.guido.addplace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.VERTICAL_VIEW
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.FragmentAddPlaceAddressDetailsBinding
import com.innoappsai.guido.databinding.FragmentAddPlaceDetailsBinding
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAddPlaceDetails :
    BaseFragment<FragmentAddPlaceDetailsBinding>(FragmentAddPlaceDetailsBinding::inflate) {

    private val viewModel: AddPlaceViewModel by activityViewModels()
    private lateinit var adapterPlaceTypes: PlacesTypeGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterPlaceTypes = PlacesTypeGroupAdapter(requireContext(), VERTICAL_VIEW)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

        }

        viewModel.apply {
            currentScreenName.collectIn(viewLifecycleOwner) {
                requireActivity().showToast("All Data Added")
            }
            error.collectIn(viewLifecycleOwner) {
                requireActivity().showToast(it)
            }
        }

    }


}