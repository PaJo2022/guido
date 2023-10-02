package com.innoappsai.guido

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.innoappsai.guido.databinding.ActivityMainBinding
import com.innoappsai.guido.fragments.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("JAPAN", "onNewIntent: ${intent?.getStringExtra("placeId")}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Log.i("JAPAN", "onNewIntent: ${intent?.data}")
        openNavFragment(
            HomeFragment(),
            supportFragmentManager,
            "HomeFragment",
            findViewById<FrameLayout>(R.id.main_fl_id)
        )

    }

    fun openNavFragment(
        fragment: Fragment,
        fragmentManager: FragmentManager,
        fragmentName: String,
        view: View,
        args: Bundle? = null
    ) {
        val ft = fragmentManager.beginTransaction()

        if (args != null) {
            fragment.arguments = args
        }

        ft.replace(view.id, fragment, fragmentName).commit()
    }



}