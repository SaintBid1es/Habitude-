package com.example.habbitapp.model.utils


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        Log.d(TAG, "BOOT_COMPLETED received")

        DailySummaryScheduler.schedule(context)
    }
}