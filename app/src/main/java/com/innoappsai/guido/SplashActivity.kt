package com.innoappsai.guido

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.innoappsai.guido.auth.AuthActivity
import com.innoappsai.guido.databinding.ActivitySplashBinding
import com.innoappsai.guido.db.AppPrefs
import com.innoappsai.guido.fragments.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    @Inject
    lateinit var appPref : AppPrefs

    private var _binding: ActivitySplashBinding? = null
    private val binding: ActivitySplashBinding get() = _binding!!

    private val viewModel : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // Check if the location permission is already granted

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1.seconds)
            viewModel.getUserData().collectIn(this@SplashActivity){
                finish()
                val isUserLoggedIn = it != null
                val naivagtingActivity = if(isUserLoggedIn) MainActivity::class.java else AuthActivity::class.java
                startActivity(Intent(this@SplashActivity, naivagtingActivity))
            }
        }


    }


}