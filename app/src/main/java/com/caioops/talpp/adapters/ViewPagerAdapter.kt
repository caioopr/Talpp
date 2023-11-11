package com.caioops.talpp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caioops.talpp.fragments.ChatsFragment
import com.caioops.talpp.fragments.ContactsFragment

class ViewPagerAdapter(private val tabs: List<String>,fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifecycle) {

    // number of tabs
    override fun getItemCount(): Int {
        return tabs.size
    }

    // creates a fragment based on the selected tab
    // (0 -> Chats, 1 -> Contacts)
    override fun createFragment(position: Int): Fragment {
        when(position){
            1 -> return ContactsFragment()
        }
        return ChatsFragment();
    }
}