package com.checkpoint.rfid_raw_material

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.checkpoint.rfid_raw_material.databinding.ActivityMainBinding
import com.checkpoint.rfid_raw_material.handheld.kt.Device
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.isGranted
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.navigation.NavigationView
import io.sentry.Sentry


class MainActivity : ActivityBase(), PermissionRequest.Listener {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: NavigationView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.optionsWriteFragment ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || "S".equals(Build.VERSION.CODENAME)) {
            requestPermissions12.addListener(this)
        } else
            requestPermissions11.addListener(this)

        runPermissions()
    }

    fun runPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || "S".equals(Build.VERSION.CODENAME))
            requestPermissions12.send()
        else
            requestPermissions11.send()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        var res: Int = 0

        result.iterator().forEachRemaining {
            if (it.isGranted()) {
                Log.d(it.permission.toString(), "aceptado")
                res++
            } else if (it.isDenied()) {
                Log.d(it.permission.toString(), "denegado")
            }
        }

        if (res > 0)
        //TODO ALERT NO PERSISSION
        else
            finish()
    }


}