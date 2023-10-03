package com.innoappsai.guido.addplace

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.innoappsai.guido.R
import com.innoappsai.guido.addplace.viewModels.AddPlaceViewModel
import com.innoappsai.guido.databinding.ActivityAddPlaceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPlaceActivity : AppCompatActivity() {

    companion object {
        fun startAddPlaceActivity(context: Context) {
            context.startActivity(Intent(context, AddPlaceActivity::class.java))
        }
    }
    private val viewModel: AddPlaceViewModel by viewModels()
    private var _binding: ActivityAddPlaceBinding? = null
    private val binding: ActivityAddPlaceBinding get() = _binding!!

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.swipeRefreshLayout.isEnabled = false

    }

    fun toggleLoading(isLoading: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isLoading
    }


}