package com.checkpoint.rfid_raw_material

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val option = ActivityOptions.makeSceneTransitionAnimation(this)
        this.lifecycle.coroutineScope.launch{
            startActivity(Intent(baseContext, MainActivity::class.java),option.toBundle())
            finish()
        }
    }
}