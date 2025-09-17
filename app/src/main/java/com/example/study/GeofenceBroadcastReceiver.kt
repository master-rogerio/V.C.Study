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
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("GeofenceBroadcastReceiver", "onReceive chamado com intent: ${intent?.action}")

        // Verificando se o intent é null
        if (intent == null) {
            Log.e("GeofenceBroadcastReceiver", "Intent é null - não é possível processar o evento")
            return
        }

        // Verificando se a ação é válida
        if (intent.action != "com.example.study.ACTION_GEOFENCE_TRANSITION") {
            Log.e("GeofenceBroadcastReceiver", "Ação inválida: ${intent.action}")
            return
        }

        // Log detalhado do intent para debug
        Log.d("GeofenceBroadcastReceiver", "Intent extras: ${intent.extras}")
        Log.d("GeofenceBroadcastReceiver", "Intent data: ${intent.data}")
        Log.d("GeofenceBroadcastReceiver", "Intent flags: ${intent.flags}")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Verificar se o evento é válido
        if (geofencingEvent == null) {
            Log.e("GeofenceBroadcastReceiver", "GeofencingEvent é null - intent não contém dados de geofencing")
            Log.d("GeofenceBroadcastReceiver", "Isso pode acontecer se o intent for disparado antes do geofence ser ativado")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = geofencingEvent.errorCode
            Log.e("Geofence", "Erro no geofence: $errorMessage")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        Log.d("GeofenceBroadcastReceiver", "Transição detectada: $geofenceTransition")
        Log.d("GeofenceBroadcastReceiver", "Geofences acionados: ${triggeringGeofences?.size ?: 0}")

        // Verificar se é uma transição de entrada ou saída
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            Log.d("GeofenceBroadcastReceiver", "Processando transição: ${if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) "ENTRADA" else "SAÍDA"}")

            triggeringGeofences?.forEach { geofence ->
                val locationId = geofence.requestId
                Log.d("GeofenceBroadcastReceiver", "Processando geofence: $locationId")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = FlashcardDatabase.getDatabase(context)
                        // CORREÇÃO: Converter String para Long
                        val location = db.favoriteLocationDao().getFavoriteLocationById(locationId.toString())

                        if (location == null) {
                            Log.w("Geofence", "Location não encontrada: $locationId")
                            return@launch
                        }

                        Log.d("GeofenceBroadcastReceiver", "Enviando notificação para: ${location.name}")

                        // CORREÇÃO: Converter List<FlashcardType> para List<String>
                        val preferredCardTypesAsString = location.preferredCardTypes.map { it.name }

                        sendNotification(
                            context = context,
                            locationName = location.name,
                            transitionType = geofenceTransition,
                            preferredCardTypes = preferredCardTypesAsString
                        )
                    } catch (e: Exception) {
                        Log.e("Geofence", "Erro ao buscar location: ${e.message}")
                    }
                }
            }
        } else {
            Log.d("GeofenceBroadcastReceiver", "Transição não é de entrada ou saída: $geofenceTransition")
        }
    }

    private fun sendNotification(
        context: Context,
        locationName: String,
        transitionType: Int,
        preferredCardTypes: List<String>
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
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Criar intent para abrir o app
        val exerciseIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("intelligentRotation", true)
            putExtra("preferredCardTypes", preferredCardTypes.toTypedArray())
            putExtra("locationId", locationName)
            putExtra("source", "geofence")
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            exerciseIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        // Criar notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora de Estudar!")
            .setContentText("Você está em $locationName. Que tal fazer alguns flashcards?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d("GeofenceBroadcastReceiver", "Notificação enviada para: $locationName")
    }
}


/*
package com.example.study

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
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

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("GeofenceBroadcastReceiver", "onReceive chamado com intent: ${intent?.action}")

        // Verificando se o intent é null
        if (intent == null) {
            Log.e("GeofenceBroadcastReceiver", "Intent é null - não é possível processar o evento")
            return
        }

        // Verificando se a ação é válida
        if (intent.action != "com.example.study.ACTION_GEOFENCE_TRANSITION") {
            Log.e("GeofenceBroadcastReceiver", "Ação inválida: ${intent.action}")
            return
        }


        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        geofencingEvent?.let { event ->
            if (event.hasError()) {
                val errorMessage = event.errorCode
                android.util.Log.e("Geofence", "Erro no geofence: $errorMessage")
                return
            }

            val geofenceTransition = geofencingEvent.geofenceTransition
            Log.d("GeofenceBroadcastReceiver", "Transição detectada: $geofenceTransition")

            if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.d("GeofenceBroadcastReceiver", "Entrando na área do geofence")
                event.triggeringGeofences?.forEach { geofence ->
                    val locationId = geofence.requestId
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = FlashcardDatabase.getDatabase(context)
                        val location = db.favoriteLocationDao().getFavoriteLocationById(locationId)

                        if (location == null) {
                            android.util.Log.w("Geofence", "Location não encontrada: $locationId")
                            return@launch
                        }

                        sendNotification(
                            context,
                            location.name,
                            locationId,
                            location.preferredCardTypes,
                            location.iconName)
                    }
                }
            }



            /*
            if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
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
            }*/
        }
    }

    private fun sendNotification(
        context: Context,
        locationName: String,
        locationId: String,
        preferredCardTypes: List<com.example.study.data.FlashcardType>,
        iconName: String
    ) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "geofence_channel",
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH // Prioridade alta
            )
            channel.description = "Notificações de geofencing para estudos"
            notificationManager.createNotificationChannel(channel)
        }

        val exerciseIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("locationId", locationId)
            putExtra("deckName", "Estudo em $locationName")
            putExtra("preferredCardTypes", preferredCardTypes.map { it.name }.toTypedArray())
            putExtra("intelligentRotation", true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, exerciseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "geofence_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hora de Estudar!")
            .setContentText("Você está em $locationName. Que tal uma sessão de estudos contextual?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Você está em $locationName. Que tal uma sessão de estudos contextual? Os flashcards preferidos para este local estão prontos para você!"))
            .build()

        notificationManager.notify(locationId.hashCode(), notification)
    }
    /*
    private fun sendNotification(context: Context, locationName: String, locationId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "geofence_channel",
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
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

     */
}

*/