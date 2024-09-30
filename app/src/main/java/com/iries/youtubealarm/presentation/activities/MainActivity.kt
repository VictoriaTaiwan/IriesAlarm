package com.iries.youtubealarm.presentation.activities

import android.Manifest
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.iries.youtubealarm.presentation.navigation.AppNavigation
import com.iries.youtubealarm.presentation.theme.IriesAlarmTheme
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            YoutubeDL.getInstance().init(applicationContext)
            // FFmpeg.getInstance().init(this);
        } catch (e: YoutubeDLException) {
            Log.e(TAG, "failed to initialize youtubedl-android", e)
        }

        enableEdgeToEdge()
        setContent {
            IriesAlarmTheme {
                AppNavigation(
                    navController = rememberNavController()
                )
            }
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (!Settings.canDrawOverlays(this))
                requestOverlayPermission()
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager: NotificationManager = getSystemService(
                NotificationManager::class.java
            )
            val areNotificationsEnabled: Boolean = notificationManager.areNotificationsEnabled()
            if (!areNotificationsEnabled)
                requestNotificationsPermission()
        }

    }

    private fun requestNotificationsPermission() {
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }
}
