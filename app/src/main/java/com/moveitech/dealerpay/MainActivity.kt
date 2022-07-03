package com.moveitech.dealerpay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
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

    companion object{
        const val  MY_PERMISSIONS_REQUEST_BLUETOOTH = 1
        const val  MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 2
        const val  MY_PERMISSIONS_REQUEST_FINE_LOCATION = 3
    }


    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // tO SHOW ACTIVITY ON FULL SCREEN //

        // tO SHOW ACTIVITY ON FULL SCREEN //
        val w = window

        w.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        setSupportActionBar(binding.toolbar)

        requestPermissions();


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




//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        val i = Intent()
//        when (item.itemId) {
//            R.id.id_tech_integration -> {
//                i.setClass(this, PaymentInteActivity::class.java)
////                i.setClass(this, MainActivityIdTech::class.java)
//                startActivity(i)
//            }
//
//        }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
//        drawerLayout.closeDrawer(GravityCompat.START)
//        return true
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_BLUETOOTH -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }



    private fun requestPermissions() {

        println("REQUEST PERMISSION IS CALLED")

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.BLUETOOTH
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission

                System.out.println("REQUEST BLUETOOTH PERMISSION IS CALLED")

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.BLUETOOTH),
                    MainActivity.MY_PERMISSIONS_REQUEST_BLUETOOTH
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission

                println("REQUEST LOCATION PERMISSION IS CALLED")

                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MainActivity.MY_PERMISSIONS_REQUEST_COARSE_LOCATION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


}
