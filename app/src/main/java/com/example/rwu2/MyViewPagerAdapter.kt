package com.example.rwu2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.gms.maps.MapFragment


class MyViewPagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return fragmentCount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BlankFragment()
            1 -> HomeFragment()
            2 -> PlusFragment()
            3 -> MyPageFragment()

            else -> HomeFragment()
        }
    }
}