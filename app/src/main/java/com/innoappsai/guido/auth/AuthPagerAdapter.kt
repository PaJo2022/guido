package com.innoappsai.guido.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class AuthPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val authFragments = arrayListOf<Fragment>(LoginFragment(), SignUpFragment())

    override fun getItemCount(): Int = authFragments.size

    override fun createFragment(position: Int): Fragment = authFragments[position]
}

