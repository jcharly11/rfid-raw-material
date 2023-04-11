package com.checkpoint.rfid_raw_material

import android.Manifest
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.fragment.NavHostFragment
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.security.jwt.JWTDecoder
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
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                 Log.e("ACCESS_FINE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_FINE_LOCATION)}")
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                 Log.e("ACCESS_COARSE_LOCATION","${permissions.getValue(Manifest.permission.ACCESS_COARSE_LOCATION)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false) -> {
                 Log.e("BLUETOOTH_SCAN","${permissions.getValue(Manifest.permission.BLUETOOTH_SCAN)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_ADMIN, false) -> {
                Log.e("BLUETOOTH_ADMIN","${permissions.getValue(Manifest.permission.BLUETOOTH_ADMIN)}")
            }
            permissions.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false) -> {
                Log.e("BLUETOOTH_CONNECT","${permissions.getValue(Manifest.permission.BLUETOOTH_CONNECT)}")
            }
            else -> {
                finish()
            }
        }
    }

    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjMwLCJkZXZpY2UiOiIzZTRhY2M0ZjVhOWI0YWRhIn0.DAB-TRIOEZy1YLqlI917TEBNQCtqMe0lPWzOzFd3Mew"

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
         val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
         val navController = navHostFragment.navController

         appBarConfiguration = AppBarConfiguration(
             setOf(R.id.optionsWriteFragment), drawerLayout
         )
         setupActionBarWithNavController(navController, appBarConfiguration)

         permissionRequest.launch(arrayOf(
             Manifest.permission.ACCESS_FINE_LOCATION,
             Manifest.permission.ACCESS_COARSE_LOCATION,
             Manifest.permission.BLUETOOTH_SCAN,
             Manifest.permission.BLUETOOTH_ADMIN,
             Manifest.permission.BLUETOOTH_CONNECT
         ))

         val decoder = JWTDecoder(token)
         var isTokenValid: Boolean = decoder.decode().isEmpty()
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}