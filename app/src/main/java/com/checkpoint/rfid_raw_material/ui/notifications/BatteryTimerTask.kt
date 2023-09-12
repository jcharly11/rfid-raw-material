package com.checkpoint.rfid_raw_material.ui.notifications

import android.util.Log
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class BatteryTimerTask {

    private var  scheduler = Executors.newScheduledThreadPool(1) as ScheduledThreadPoolExecutor
    private lateinit var taskHandler:  ScheduledFuture<*>

    fun startTimer(battery:() -> Unit) {
        Log.e("statrting task battery","---->")
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                battery()
            }
        },0L,12000)

    }
    fun stopTimer(){
        taskHandler.cancel(true)
        scheduler.purge()

    }

}