package com.guido.app.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.guido.app.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {


    private var _binding: ActivityAuthBinding? = null
    private val binding: ActivityAuthBinding get() = _binding!!

    private lateinit var authViewPagerAdapter: AuthPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        authViewPagerAdapter = AuthPagerAdapter(this)


        binding.authViewPager.adapter = authViewPagerAdapter

        // Check if the location permission is already granted
        binding.authViewPager.isUserInputEnabled = false
    }

    fun goToActivity(position: Int) {
        binding.authViewPager.setCurrentItem(position, false)
    }


}