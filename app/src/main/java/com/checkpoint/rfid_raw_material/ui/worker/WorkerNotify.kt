package com.checkpoint.rfid_raw_material.ui.worker

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.checkpoint.rfid_raw_material.MainActivity
import com.checkpoint.rfid_raw_material.ui.notifications.NotificationBuilder

class WorkerNotify (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val context =applicationContext

        var resultIntent = Intent(context, MainActivity::class.java)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val icon = Icon.createWithResource(applicationContext, android.R.drawable.ic_dialog_alert)
        NotificationBuilder(notificationManager).getNotification(applicationContext ,resultIntent,icon)

        return Result.success()
    }
}