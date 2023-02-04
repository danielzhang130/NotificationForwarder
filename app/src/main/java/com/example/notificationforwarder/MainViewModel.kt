package com.example.notificationforwarder

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ViewModel() {

    fun sendNotification(token: String) {
        coroutineScope.launch {

            val message: Message = Message.builder()
                .setNotification(
                    Notification.builder()
                        .setTitle("\$GOOG up 1.43% on the day")
                        .setBody("\$GOOG gained 11.80 points to close at 835.67, up 1.43% on the day.")
                        .build()
                )
                .setToken(token)
                .build()

            // Send a message to devices subscribed to the combination of topics
            // specified by the provided condition.
            val response = FirebaseMessaging.getInstance().send(message)
            // Response is a message ID string.
            Log.d("", "Successfully sent message: $response")
        }
    }

    override fun onCleared() {
        coroutineScope.cancel()
    }
}