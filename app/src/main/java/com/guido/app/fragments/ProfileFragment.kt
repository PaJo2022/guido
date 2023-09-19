package com.guido.app.fragments


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.guido.app.BaseFragment
import com.guido.app.R
import com.guido.app.adapters.PlacesTypeChipAdapter
import com.guido.app.adapters.PlacesTypeGroupAdapter
import com.guido.app.adapters.VerticalGridCustomItemDecoration
import com.guido.app.databinding.FragmentProfileBinding
import com.guido.app.db.AppPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private lateinit var thumbView: View
    private lateinit var viewModel: ProfileViewModel
    private lateinit var placesTypeGroupAdapter: PlacesTypeGroupAdapter

    @Inject
    lateinit var appPrefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(requireContext())
        thumbView = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_seekbar_thumb, null, false)
    }

    override fun onResume() {
        super.onResume()
        val currentDistanceInPref = appPrefs.prefDistance
        binding.seekbarDistance.progress = currentDistanceInPref / 1000
        binding.tvDistance.text = "${currentDistanceInPref / 1000} Km"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            seekbarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvDistance.text = "$p1 Km"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    viewModel.distanceProgress = p0?.progress ?: return

                }

            })

            rvInterests.apply {
                addItemDecoration(VerticalGridCustomItemDecoration(requireContext()))
                adapter = placesTypeGroupAdapter
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            }

        }

        viewModel.apply {
            userInterestes.observe(viewLifecycleOwner){
                placesTypeGroupAdapter.setPlacesType(it)
            }

        }
        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            viewModel.onPlaceInterestClicked(it.id)
        }

    }




}