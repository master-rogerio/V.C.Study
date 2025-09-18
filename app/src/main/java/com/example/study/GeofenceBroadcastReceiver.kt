package com.example.study

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.study.data.FlashcardDatabase
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : android.content.BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "geofence_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "GeofenceBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive chamado com intent: ${intent?.action}")

        if (intent == null) {
            Log.e(TAG, "Intent é null - não é possível processar o evento")
            return
        }

        if (intent.action != "com.example.study.ACTION_GEOFENCE_TRANSITION") {
            Log.e(TAG, "Ação inválida: ${intent.action}")
            return
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent é null - intent não contém dados de geofencing")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = geofencingEvent.errorCode
            Log.e(TAG, "Erro no geofence: $errorMessage")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        Log.d(TAG, "Transição detectada: $geofenceTransition")
        Log.d(TAG, "Geofences acionados: ${triggeringGeofences?.size ?: 0}")

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "Entrando na área do geofence")

            triggeringGeofences?.forEach { geofence ->
                val locationId = geofence.requestId
                Log.d(TAG, "Processando geofence: $locationId")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = FlashcardDatabase.getDatabase(context)
                        val location = db.favoriteLocationDao().getFavoriteLocationById(locationId)

                        if (location == null) {
                            Log.w(TAG, "Location não encontrada: $locationId")
                            return@launch
                        }

                        Log.d(TAG, "Enviando notificação para: ${location.name}")

                        val preferredCardTypesAsString = location.preferredCardTypes.map { it.name }

                        sendNotification(
                            context = context,
                            locationName = location.name,
                            transitionType = geofenceTransition,
                            preferredCardTypes = preferredCardTypesAsString,
                            locationId = locationId
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro ao buscar location: ${e.message}")
                    }
                }
            }
        } else {
            Log.d(TAG, "Transição não é de entrada: $geofenceTransition")
        }
    }

    private fun sendNotification(
        context: Context,
        locationName: String,
        transitionType: Int,
        preferredCardTypes: List<String>,
        locationId: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Criar canal de notificação se necessário
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de geofence para estudo"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Criar intent para abrir o app com rotação inteligente
        val exerciseIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("intelligentRotation", true)
            putExtra("preferredCardTypes", preferredCardTypes.toTypedArray())
            putExtra("locationId", locationId)
            putExtra("source", "geofence")
            putExtra("locationName", locationName)
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            locationId.hashCode(), // ID único para cada localização
            exerciseIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // Criar notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora de Estudar! 📚")
            .setContentText("Você está em $locationName. Que tal fazer alguns flashcards?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Você está em $locationName. Que tal uma sessão de estudos contextual? Os flashcards preferidos para este local estão prontos para você!"))
            .build()

        notificationManager.notify(locationId.hashCode(), notification)
        Log.d(TAG, "Notificação enviada para: $locationName")
    }
}