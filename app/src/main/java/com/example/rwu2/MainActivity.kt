package com.example.rwu2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import com.example.rwu2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val iconarr = arrayListOf<Int>(R.drawable.ic_baseline_map_24, R.drawable.ic_baseline_filter_9_plus_24, R.drawable.ic_baseline_settings_24)
    lateinit var myadapter:MyViewPagerAdapter
    lateinit var bottomTab : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragment()

    }

    private fun initFragment() {
//        binding.viewpager.adapter = MyViewPagerAdapter(this)
//        TabLayoutMediator(binding.tablayout, binding.viewpager){
//            tab , positon ->
//            tab.setIcon(iconarr[positon])
//        }.attach()

        myadapter = MyViewPagerAdapter(supportFragmentManager, 4)
        binding.viewpager.adapter = myadapter
        binding.tablayout.setupWithViewPager(binding.viewpager)


        binding.viewpager.setCurrentItem(1)
        bottomTab = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        binding.tablayout.getTabAt(1)!!.customView = bottomTab.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        binding.tablayout.getTabAt(0)!!.customView = bottomTab.findViewById(R.id.btn_bottom_navi_map_tab) as RelativeLayout
        binding.tablayout.getTabAt(2)!!.customView = bottomTab.findViewById(R.id.btn_bottom_navi_plus_tab) as RelativeLayout
        binding.tablayout.getTabAt(3)!!.customView = bottomTab.findViewById(R.id.btn_bottom_navi_setting_tab) as RelativeLayout



    }

//    fun updateplus(){
//        bottomTab.findViewById<RelativeLayout>(R.id.btn_bottom_navi_plus_tab).setOnClickListener {
//            var s = myadapter.getItem(2) as PlusFragment
//            s.updateData()
//        }
//    }
}
