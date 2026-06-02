package mx.edu.unpa.adoptame.model

import com.google.gson.annotations.SerializedName

/**
 * Mapeado 1:1 con SolicitudAdopcionResponseDTO.
 * estadoSolicitud es String para coincidir con el backend.
 */
data class SolicitudAdopcion(
    @SerializedName("idSolicitud")           val idSolicitud: Int,
    @SerializedName("idMascota")             val idMascota: Int,
    @SerializedName("idUsuarioSolicitante")  val idUsuarioSolicitante: Int,
    @SerializedName("mensaje")               val mensaje: String?,
    @SerializedName("estadoSolicitud")       val estadoSolicitud: String,  // "PENDIENTE" | "APROBADA" | "RECHAZADA"
    @SerializedName("fechaSolicitud")        val fechaSolicitud: String?
)
