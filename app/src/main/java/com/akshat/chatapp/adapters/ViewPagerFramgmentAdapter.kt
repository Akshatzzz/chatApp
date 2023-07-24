package com.akshat.chatapp.adapters

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akshat.chatapp.fragments.CallsFragment
import com.akshat.chatapp.fragments.ChatsFragment
import com.akshat.chatapp.fragments.StatusFragment

class ViewPagerFramgmentAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        // hardcoded because we know its just three
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> StatusFragment()
            2 -> CallsFragment()
            else -> ChatsFragment()
        }
    }

}