package com.moveitech.dealerpay

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.moveitech.dealerpay.databinding.ActivityMainBinding
import com.moveitech.dealerpay.databinding.NavigationDrawerHeaderBinding
import com.moveitech.dealerpay.util.gone
import com.moveitech.dealerpay.util.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setUpNavigation()
        setupNavigationDrawerHeader()
        btnListener()
    }

    private fun btnListener() {

        binding.ivProfile.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }
    }

    private fun setupNavigationDrawerHeader() {
        val headerView: View = binding.navView.getHeaderView(0)
        val headerBinding: NavigationDrawerHeaderBinding =
            NavigationDrawerHeaderBinding.bind(headerView)

        headerBinding.parentLayout.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }
    }



    private fun setUpNavigation() {

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
        navController = navHostFragment.navController
        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.cardPaymentFragment,
                R.id.paymentRequestOne
            )
                .setOpenableLayout(binding.drawerLayout)
                .build()
        setupWithNavController(binding.navView, navController)
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {

                R.id.homeFragment -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_humburger_icon)

                }
//                R.id.loginFragment -> {
//
//                }
                R.id.cardPaymentFragment -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_humburger_icon)
                }
                R.id.paymentRequestOne -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_humburger_icon)
                }
                R.id.cardPaymentFragmentTwo -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
                }
                R.id.paymentReqTwoFragment -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
                }
                R.id.settingsFragment -> {
                    binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
                    binding.drawerLayout.closeDrawer(Gravity.LEFT, true)
                }
            }
        }
    }


    fun setDefaultUi(showToolbar: Boolean, showNavigationDrawer: Boolean, showProfilePic: Boolean) {
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
        if (showProfilePic) {
            binding.ivProfile.visible()
        } else {
            binding.ivProfile.gone()
        }

    }



    override fun onBackPressed() {
        super.onBackPressed()

    }

    private fun manageBackStack()
    {
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, true)
            .build()
        when {
            navController.currentDestination?.id ?:0 == R.id.cardPaymentFragment -> {
                navController.navigate(R.id.homeFragment, null,navOptions = navOptions)
            }
            navController.currentDestination?.id ?:0 == R.id.paymentRequestOne -> {
                navController.navigate(R.id.homeFragment, null,navOptions = navOptions)
            }
            navController.currentDestination?.id ?:0 == R.id.homeFragment -> {
                finish()
            }
        }

    }



}