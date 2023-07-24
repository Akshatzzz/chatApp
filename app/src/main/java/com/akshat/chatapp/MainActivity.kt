package com.akshat.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.akshat.chatapp.adapters.ViewPagerFramgmentAdapter
import com.akshat.chatapp.databinding.ActivityMainBinding
import com.akshat.chatapp.databinding.ActivitySignInBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainActivityToolBar.root)
        setupViewsWithAdaper()
        addTabLayoutMediator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this@MainActivity).inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                Toast.makeText(this, "Soon settings will come", Toast.LENGTH_SHORT).show()
            }

            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                this@MainActivity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewsWithAdaper() {
        val pagerAdapter = ViewPagerFramgmentAdapter(this@MainActivity)
        binding.viewPager.adapter = pagerAdapter
    }
    private fun addTabLayoutMediator() {
        val listOfTitles  = listOf("CHAT","STATUS","CALLS")
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
                tab.text = listOfTitles[position]
        }.attach()
    }
}