package com.guido.app

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.guido.app.databinding.ActivityMainBinding
import com.guido.app.fragments.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        openNavFragment(
            HomeFragment(),
            supportFragmentManager,
            "HomeFragment",
            findViewById<FrameLayout>(R.id.fl_id)
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