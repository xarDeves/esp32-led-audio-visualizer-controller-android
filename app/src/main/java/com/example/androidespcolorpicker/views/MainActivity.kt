package com.example.androidespcolorpicker.views

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.androidespcolorpicker.R
import com.example.androidespcolorpicker.adapters.MainViewPagerAdapter
import com.example.androidespcolorpicker.viewmodels.MainActivityViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var fragments: Array<Fragment>

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: MainViewPagerAdapter
    private lateinit var tabLayout: TabLayout

    override fun onBackPressed() {
        val currentItem = viewPager.currentItem
        if (currentItem != 0) {
            viewPager.setCurrentItem(currentItem - 1, true)
            return
        }
        finish()
    }

    /*private fun initializeData() {
        Database.setFilesDir(this.filesDir)
        Database.read()
    }*/

    private fun fetchXmlElements() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupViewPager() {
        adapter = MainViewPagerAdapter(fragments, this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
    }

    private fun setupTabs() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Picker"
                1 -> tab.text = "Pallete"
            }
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        val colorsFragment = ColorPalleteFragment()
        val colorPickerFragment = ColorPickerFragment()

        fragments = arrayOf(colorPickerFragment, colorsFragment)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        fetchXmlElements()
        setupViewPager()
        setupTabs()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0 && colorsFragment.isVisible) colorsFragment.changeAdapterButtonState()
            }
        })

    }

}