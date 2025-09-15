package com.example.study

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.study.data.FlashcardDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Corrected: Using a safe call with ?.let to handle nullable object
        geofencingEvent?.let { event ->
            if (event.hasError()) {
                return
            }

            if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                // Corrected: Using a safe call for the list of geofences
                event.triggeringGeofences?.forEach { geofence ->
                    val locationId = geofence.requestId
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = FlashcardDatabase.getDatabase(context)
                        val location = db.favoriteLocationDao().getFavoriteLocationById(locationId)
                        location?.let {
                            sendNotification(context, it.name, locationId)
                        }
                    }
                }
            }
        }
    }

    private fun sendNotification(context: Context, locationName: String, locationId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("geofence_channel", "Geofence Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val exerciseIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("deckName", "Estudo em $locationName")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, exerciseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "geofence_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app's icon
            .setContentTitle("Hora de Estudar!")
            .setContentText("Você está em $locationName. Que tal uma sessão de estudos contextual?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(locationId.hashCode(), notification) // Use a unique ID for each notification
    }
}