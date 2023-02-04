package com.example.notificationforwarder

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
        Log.d("", "new notification " + sbn)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope = scope

        with(packageManager) {
            val title = sbn.notification.extras.getString("android.title")
            val text = sbn.notification.extras.getString("android.text")

            @Suppress("DEPRECATION")
            val appName = getApplicationLabel(
                getApplicationInfo(sbn.packageName, 0)
            )
            val context = "Forwarded from $appName"

            scope.launch {
                val token = getSharedPreferences("default", 0).getString("code", null) ?: return@launch
                val message: Message = Message.builder()
                    .setNotification(
                        Notification.builder()
                            .setTitle(title)
                            .setBody("$text\n$context")
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