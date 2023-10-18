package com.innoappsai.guido



import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity

object FragmentUtils {

    fun replaceFragment(activity: AppCompatActivity, containerId: Int, fragment: Fragment) {
        // Get the FragmentManager
        val fragmentManager: FragmentManager = activity.supportFragmentManager

        // Start a new FragmentTransaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the contents of the fragment container with the new fragment
        transaction.replace(containerId, fragment)

        // Add the transaction to the back stack (optional, allows navigating back)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }
}