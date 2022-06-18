package com.moveitech.dealerpay

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.moveitech.dealerpay.databinding.ActivityMainBinding
import com.moveitech.dealerpay.util.gone
import com.moveitech.dealerpay.util.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setUpNavigation()


        drawerListener()


    }


    private fun setUpNavigation() {

        binding.toolbar.setNavigationIcon(R.drawable.ic_humburger_icon)
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(R.id.homeFragment)
                .setOpenableLayout(binding.drawerLayout)
                .build()
        setupWithNavController(binding.navView, navController)
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            if (destination.id == R.id.homeFragment) {
                binding.toolbar.setNavigationIcon(R.drawable.ic_humburger_icon)

            }
        }


    }


    private fun drawerListener() {
        binding.navView.setNavigationItemSelectedListener {

            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

//                blur()
            }

            false
        }
    }

    fun setDefaultUi(showToolbar: Boolean, showNavigationDrawer: Boolean) {
        if (showNavigationDrawer) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }
        if (showToolbar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }

    }
}