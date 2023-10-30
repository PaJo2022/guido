package com.innoappsai.guido.generateItinerary.screens

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.BaseFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.databinding.FragmentPlaceDreamCardBinding
import com.innoappsai.guido.generateItinerary.adapters.DreamCardAdapter
import com.innoappsai.guido.generateItinerary.adapters.itemdecorator.DreamCardItemDecorator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentPlaceDreamCard
    : BaseFragment<FragmentPlaceDreamCardBinding>(FragmentPlaceDreamCardBinding::inflate) ,View.OnClickListener {

    private lateinit var dreamCardAdapter: DreamCardAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dreamCardAdapter = DreamCardAdapter(requireContext())


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val llManager: GridLayoutManager = object : GridLayoutManager(requireContext() , 3,
            VERTICAL,false) {
            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
                val smoothScroller: LinearSmoothScroller =
                    object : LinearSmoothScroller(requireContext()) {
                        private val SPEED = 4000f // Change this value (default=25f)
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return SPEED
                        }
                    }
                smoothScroller.targetPosition = position
                startSmoothScroll(smoothScroller)
            }
        }
        binding.apply {
            rvDreamCards.apply {
                addItemDecoration(DreamCardItemDecorator(requireContext()))
                addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        // true: consume touch event
                        // false: dispatch touch event
                        return true
                    }
                })
                adapter = dreamCardAdapter
                layoutManager = llManager
                setHasFixedSize(true)

            }
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.fragmentChooseNearByPlacesOptions)
            }
        }
        dreamCardAdapter.setDreamCardItems(gridItems)
        handler.postDelayed(runnable, speedScroll.toLong())
    }

    private val gridItems = listOf(
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3,
        R.drawable.dread_card1,
        R.drawable.dread_card2,
        R.drawable.dread_card3
        // Add more items here
    )





    private val handler = Handler()
    private val speedScroll = 0
    private val runnable = object : Runnable {
        var count = 0
        override fun run() {
            if (count == dreamCardAdapter.itemCount) count = 0
            if (count < dreamCardAdapter.itemCount) {
                binding.rvDreamCards.smoothScrollToPosition(++count)
                handler.postDelayed(this, speedScroll.toLong())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, speedScroll.toLong())
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onClick(v: View?) {

    }


}