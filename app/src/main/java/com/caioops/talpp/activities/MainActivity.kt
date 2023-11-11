package com.caioops.talpp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.caioops.talpp.R
import com.caioops.talpp.adapters.ViewPagerAdapter
import com.caioops.talpp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarInitializer()
        pageNavigationInitializer()
    }

    private fun pageNavigationInitializer(){
        val tabLayout = binding.mainTabLayout
        val viewPager = binding.mainViewPager

        val tabs = listOf("CHATS","CONTACTS")

        viewPager.adapter = ViewPagerAdapter(tabs, supportFragmentManager, lifecycle);

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text= tabs[position]
        }.attach() // the tabLayout to viewPager
    }

    private fun toolbarInitializer() {
        val toolbar = binding.includeMainToolbar.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Talpp"
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.item_profile -> {
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        }
                        R.id.item_logout -> {
                            logoutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun logoutUser() {
        AlertDialog.Builder(this)
            .setTitle("Log out")
            .setMessage("Confirm to exit")
            .setNegativeButton("No"){ dialog,position -> }
            .setPositiveButton("Yes"){ dialog,position ->
                firebaseAuth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            .create()
            .show()
    }

}