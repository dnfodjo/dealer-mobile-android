package com.moveitech.dealerpay

import android.R.attr.bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.moveitech.dealerpay.databinding.ActivityMainBinding
import com.moveitech.dealerpay.util.ScreenShotUtil
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setUpNavigation()


        drawerListener()
        blurBackGround()
    }
    private fun setUpNavigation() {

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        val appBarConfiguration: AppBarConfiguration = AppBarConfiguration.Builder(navController.graph)
            .setOpenableLayout(binding.drawerLayout)
            .build()
        setupWithNavController(binding.navView, navController)
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)

    }

    private fun blurBackGround()
    {

        val bitmap=ScreenShotUtil.takeScreenshot(binding.drawerLayout)
        binding.blurView.setImageBitmap(bitmap)
        binding.childLayout.visibility= View.GONE
        binding.blurLayout.startBlur()
    }

    fun drawerListener()
    {
        binding.navView.setNavigationItemSelectedListener {

            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {

                blurBackGround()
            }

            false
        }
    }
}