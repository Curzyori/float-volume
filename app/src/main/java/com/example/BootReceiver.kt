package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("floating_volume_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("service_enabled", false)
            if (isEnabled) {
                val serviceIntent = Intent(context, VolumeFloatingService::class.java)
                try {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } catch (e: Exception) {
                    // Safety: Android 12+ FGS limitations are exempted during BOOT_COMPLETED
                    e.printStackTrace()
                }
            }
        }
    }
}
