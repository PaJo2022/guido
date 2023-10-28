package com.innoappsai.guido



import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

object FragmentUtils {

    fun replaceFragment(
        activity: AppCompatActivity,
        containerId: Int,
        fragment: Fragment,
        args: Bundle? = null,
        shouldAllowBackStack : Boolean = false
    ) {
        // Get the FragmentManager
        val fragmentManager: FragmentManager = activity.supportFragmentManager

        // Start a new FragmentTransaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragment.arguments = args
        // Replace the contents of the fragment container with the new fragment
        transaction.replace(containerId, fragment)

        // Add the transaction to the back stack (optional, allows navigating back)
        transaction.addToBackStack(if(shouldAllowBackStack) fragment.javaClass.name else null)

        // Commit the transaction
        transaction.commit()
    }
}