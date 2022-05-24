package com.example.androidespcolorpicker.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androidespcolorpicker.views.ColorPalleteFragment

class MainViewPagerAdapter(
    private val fragments: Array<Fragment>,
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {
 
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}