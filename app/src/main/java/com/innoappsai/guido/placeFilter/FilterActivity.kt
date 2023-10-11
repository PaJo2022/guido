package com.innoappsai.guido.placeFilter

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.innoappsai.guido.MyApp
import com.innoappsai.guido.adapters.PlaceFeaturesAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter
import com.innoappsai.guido.adapters.PlacesTypeGroupAdapter.Companion.PlaceViewType.CHIPS_VIEW
import com.innoappsai.guido.collectIn
import com.innoappsai.guido.databinding.ActivityFilterBinding
import com.innoappsai.guido.toggleEnableAndAlpha
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterActivity : AppCompatActivity() {

    private val viewModel: FilterActivityVideModel by viewModels()
    private var _binding: ActivityFilterBinding? = null
    private val binding: ActivityFilterBinding get() = _binding!!
    private lateinit var placesTypeGroupAdapter: PlacesTypeGroupAdapter
    private lateinit var adapterPlaceFeatures: PlaceFeaturesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        placesTypeGroupAdapter = PlacesTypeGroupAdapter(this, CHIPS_VIEW)
        adapterPlaceFeatures = PlaceFeaturesAdapter(this)

        binding.apply {
            swipeRefreshLayout.isEnabled = false
            rvPlaceFeatures.apply {
                adapter = adapterPlaceFeatures
                layoutManager =
                    LinearLayoutManager(this@FilterActivity, LinearLayoutManager.VERTICAL, false)
            }
            rvPlaceTypes.apply {
                adapter = placesTypeGroupAdapter
                layoutManager =
                    LinearLayoutManager(this@FilterActivity, LinearLayoutManager.VERTICAL, false)
            }
            seekbarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvDistance.text = "$p1 Km"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    viewModel.onDistanceChanged(p0?.progress ?: return)
                }

            })
            btnSaveChanges.setOnClickListener {
                val isSavedEnabled = binding.cbSaveFilter.isChecked
                viewModel.savePlaceTypePreferences(isSavedEnabled)
                MyApp.isNewInterestsSet = true
            }
            tvReset.setOnClickListener {
                viewModel.getSavedPlaceTypePreferences()
            }
        }

        viewModel.apply {
            userInterestes.observe(this@FilterActivity) {
                placesTypeGroupAdapter.setPlacesType(it)
            }
            placeFeatures.observe(this@FilterActivity) {
                adapterPlaceFeatures.setPlaceFeatures(it)
            }
            isPlaceInterestesSaved.collectIn(this@FilterActivity) {
                finish()
            }
            newInterestsSelected.observe(this@FilterActivity) {
                binding.tvReset.toggleEnableAndAlpha(it)
                binding.btnSaveChanges.toggleEnableAndAlpha(it)
            }
            isLoading.collectIn(this@FilterActivity) {
                binding.swipeRefreshLayout.isRefreshing = it
            }
        }
        adapterPlaceFeatures.setOnPlaceFeatureSelected {
            viewModel.onPlaceFeatureClicked(it)
        }
        placesTypeGroupAdapter.setOnPlaceTypeSelected {
            viewModel.onPlaceInterestClicked(it.id)
        }
    }


    override fun onResume() {
        super.onResume()
        val currentDistanceInPref = viewModel.appPrefs.prefDistance
        binding.seekbarDistance.progress = currentDistanceInPref / 1000
        binding.tvDistance.text = "${currentDistanceInPref / 1000} Km"
    }

}