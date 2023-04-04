package com.checkpoint.rfid_raw_material

import android.Manifest.permission.*
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.utils.CustomBattery

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var btnHandHeldGun: AppCompatImageView? = null
    var batteryView: CustomBattery? = null
    var btnCreateLog: AppCompatImageView? = null
    var lyCreateLog: LinearLayout? = null
     val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
             //   Log.d("ACCESS_FINE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_FINE_LOCATION)}")
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                //    Log.d("ACCESS_COARSE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_COARSE_LOCATION)}")
            }
            permissions.getOrDefault(BLUETOOTH_SCAN, false) -> {
                //    Log.d("BLUETOOTH_SCAN","${permissions.getValue(Manifest.permission.BLUETOOTH_SCAN)}")
            }
            permissions.getOrDefault(BLUETOOTH_ADMIN, false) -> {
                //    Log.d("BLUETOOTH_ADMIN","${permissions.getValue(Manifest.permission.BLUETOOTH_ADMIN)}")
            }
            permissions.getOrDefault(BLUETOOTH_CONNECT, false) -> {
                //    Log.d("BLUETOOTH_CONNECT","${permissions.getValue(Manifest.permission.BLUETOOTH_CONNECT)}")
            }
            else -> {
                finish()
            }
        }
    }


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        btnHandHeldGun= binding.appBarMain.imgHandHeldGun
        btnCreateLog= binding.appBarMain.imgCreateLog
        batteryView = binding.appBarMain.batteryView
        lyCreateLog = binding.appBarMain.lyCreateLog

        batteryView!!.visibility = View.GONE
        btnHandHeldGun!!.visibility = View.GONE
        lyCreateLog!!.visibility = View.GONE
         val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.optionsWriteFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        permissionRequest.launch(arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                BLUETOOTH_SCAN,
                BLUETOOTH_ADMIN,
                BLUETOOTH_CONNECT
            ))

    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}