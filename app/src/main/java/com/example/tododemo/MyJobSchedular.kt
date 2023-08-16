package com.example.tododemo

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


class MyJobScheduler : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        var title = params?.extras?.getString(NOTIFICATION_TITLE)
        var message = params?.extras?.getString(NOTIFICATION_MESSAGE)

        showNotification(title, message)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }

    private fun showNotification(title: String?, message: String?) {
        val resultIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_splashscreen)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
