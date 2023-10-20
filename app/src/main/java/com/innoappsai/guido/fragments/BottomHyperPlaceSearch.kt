package com.innoappsai.guido.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.innoappsai.guido.databinding.BottomsheetHyperLocalPlaceSearchBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.openAppSettings
import com.innoappsai.guido.services.HyperLocalPlacesSearchService
import com.innoappsai.guido.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomHyperPlaceSearch : BottomSheetDialogFragment() {


    private var _binding: BottomsheetHyperLocalPlaceSearchBinding? = null
    private val binding: BottomsheetHyperLocalPlaceSearchBinding get() = _binding!!

    @Inject
    lateinit var appPrefs: AppPrefs

    private fun setHeightAsContentHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setHeightAsContentHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        _binding = BottomsheetHyperLocalPlaceSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            toggleTts.isChecked = appPrefs.isTTSEnabled
            seekbarTime.progress = appPrefs.hyperLocalSearchPoolingTime.toInt()
            tvMinTime.text = "${appPrefs.hyperLocalSearchPoolingTime} Minutes"
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toggleTts.setOnCheckedChangeListener { compoundButton, b ->
                appPrefs.isTTSEnabled = b
            }
            toggleTts.apply {
                text = null
                textOn = null
                textOff = null
            }
            btnPermission.setOnClickListener {
                checkLocationPermission()
            }
            ivClose.setOnClickListener {
                dismiss()
            }
            seekbarTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.tvMinTime.text = "${p1} Minutes"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    appPrefs.hyperLocalSearchPoolingTime = p0?.progress?.toLong() ?: 1

                }

            })
        }

    }

    private fun checkLocationPermission() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted && coarseLocationGranted) {
            askForNotificationPermission()
        } else {
            // Permission(s) is/are not yet granted, request them
            val permissionsToRequest = mutableListOf<String>()
            if (!fineLocationGranted) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (!coarseLocationGranted) {
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }

            locationPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }


    private fun startHyperPlaceService() {
        val serviceIntent =
            Intent(requireContext(), HyperLocalPlacesSearchService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent)
        } else {
            requireActivity().startService(serviceIntent)
        }
        dismiss()
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startHyperPlaceService()
            } else {
                notificationPermission.launch(permission)
            }
        } else {
            startHyperPlaceService()
        }
    }

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startHyperPlaceService()
            } else {
                // Permission is denied (user selected "Deny" but not "Never ask again")
                // You can handle this case by showing a rationale
                requireActivity().showToast("Please allow push notification sho we can send you the notifications")
            }
        }


    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted && coarseLocationGranted) {
                // Both permissions are granted
                // Get the current location
                askForNotificationPermission()
            } else {
                // Handle the denied or permanently denied cases for either permission
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    // Permission is permanently denied (user selected "Never ask again")
                    // You may inform the user or redirect them to app settings
                    requireActivity().openAppSettings()
                } else {
                    // Permission is denied (user selected "Deny" but not "Never ask again")
                    // You can handle this case by showing a rationale
                    requireActivity().showToast("Please allow location permission show we can help you with more personalzied travel expereince")
                }
            }
        }


}