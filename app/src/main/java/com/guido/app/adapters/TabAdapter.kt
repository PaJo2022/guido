package com.guido.app.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class TabAdapter(fragmentManager: FragmentManager, lifecycle : Lifecycle) :
    FragmentStateAdapter(fragmentManager,lifecycle) {

    private var _fragments : List<Fragment> = emptyList()
    fun setFragmentsItems(newFragments : List<Fragment>){
        _fragments = newFragments
        notifyDataSetChanged()
    }
    override fun getItemCount() = _fragments.size

    override fun createFragment(position: Int) = _fragments[position]


}