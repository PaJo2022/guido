package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.asynctaskcoffee.cardstack.CardListener
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.addOnBackPressedCallback
import com.innoappsai.guido.databinding.FragmentChooseNearByPlacesBinding
import com.innoappsai.guido.databinding.FragmentItinearyGenerationStep1Binding
import com.innoappsai.guido.generateItinerary.ViewModelGenerateItinerary
import com.innoappsai.guido.generateItinerary.adapters.ItineraryOptionPagerAdapter
import com.innoappsai.guido.generateItinerary.model.itineraryGenerationOptionList
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelBudgetType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelCompanionType
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelDuration
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelEachDayTiming
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelInterests
import com.innoappsai.guido.generateItinerary.screens.options.FragmentTravelStartDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentChooseNearByPlacesOptions :
    BaseFragment<FragmentChooseNearByPlacesBinding>(FragmentChooseNearByPlacesBinding::inflate),
    CardListener {

    private val viewModel: ViewModelGenerateItinerary by activityViewModels()
    private lateinit var adapter: ItineraryOptionPagerAdapter

    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItineraryOptionPagerAdapter(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onLeftSwipe(position: Int, model: Any) {

    }

    override fun onRightSwipe(position: Int, model: Any) {

    }

    override fun onItemShow(position: Int, model: Any) {

    }

    override fun onSwipeCancel(position: Int, model: Any) {

    }

    override fun onSwipeCompleted() {

    }
}