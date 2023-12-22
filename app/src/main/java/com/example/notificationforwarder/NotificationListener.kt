package com.example.notificationforwarder

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private var coroutineScope: CoroutineScope? = null

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope?.cancel()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope = scope

        with(packageManager) {
            val title = sbn.notification.extras.get("android.title") ?: return
            val text = sbn.notification.extras.get("android.text") ?: ""

            val appName = getApplicationLabel(
                getApplicationInfo(sbn.packageName, PackageManager.ApplicationInfoFlags.of(0L))
            )
            val context = "Forwarded from $appName"

            scope.launch {
                val token = getSharedPreferences("default", 0).getString("code", null) ?: return@launch
                val message: Message = Message.builder()
                    .setNotification(
                        Notification.builder()
                            .setTitle(title.toString())
                            .setBody("$text\n$context".trim())
                            .build()
                    )
                    .setToken(token)
                    .build()

                val response = FirebaseMessaging.getInstance().send(message)
                // Response is a message ID string.
                Log.d("", "Successfully sent message: $response")
            }
        }
    }
}