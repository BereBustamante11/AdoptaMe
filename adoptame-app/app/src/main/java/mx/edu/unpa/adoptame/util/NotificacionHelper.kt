package mx.edu.unpa.adoptame.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import mx.edu.unpa.adoptame.R

object NotificacionHelper {

    private const val CANAL_ID    = "adoptame_solicitudes"
    private const val CANAL_NOMBRE = "Solicitudes de adopción"
    private const val CANAL_DESC  = "Notificaciones sobre el estado de tus solicitudes"

    /** Crea el canal (requerido en Android 8+). Llamar en Application o al inicio de la app. */
    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_ID,
            CANAL_NOMBRE,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = CANAL_DESC }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(canal)
    }

    /**
     * Muestra una notificación local indicando que la adopción fue aprobada.
     * @param nombreMascota  Nombre de la mascota adoptada.
     * @param notifId        ID único para poder actualizar o cancelar la notif si hace falta.
     */
    fun notificarAdopcionAprobada(
        context: Context,
        nombreMascota: String,
        notifId: Int = System.currentTimeMillis().toInt()
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notif = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle("🎉 ¡Adopción aprobada!")
            .setContentText("Tu solicitud para adoptar a $nombreMascota fue aceptada.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tu solicitud para adoptar a $nombreMascota fue aprobada. ¡El donador se pondrá en contacto contigo pronto!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(notifId, notif)
    }
}
