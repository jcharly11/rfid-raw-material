package com.checkpoint.rfid_raw_material.ui.notifications

import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.OperationFailureException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.xml.datatype.DatatypeConstants.SECONDS


class BatteryTimerTask {

    private var  scheduler = Executors.newScheduledThreadPool(1) as ScheduledThreadPoolExecutor
    private lateinit var taskHandler:  ScheduledFuture<*>

    fun startTimer(battery:() -> Unit) {
        if (scheduler == null) {

            val task = Runnable {
                    battery()
            }
            taskHandler = scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS)
         }

    }
    fun stopTimer(){
        taskHandler.cancel(true)
        scheduler.purge()

    }

}