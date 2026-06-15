package mx.edu.unpa.adoptame.model

/**
 * Mapea SolicitudAdopcionResponseDTO del backend.
 *
 * Los campos con JOIN (nombreMascota, nombreSolicitante) son nullable porque
 * podrían no estar presentes en todos los endpoints (p.ej. POST /solicitudes
 * solo confirma la creación y puede no retornar los datos enriquecidos).
 */
data class SolicitudAdopcion(
    val idSolicitud: Int,
    val idMascota: Int,
    val nombreMascota: String?,         // JOIN: Mascota.nombre
    val idUsuarioSolicitante: Int,
    val nombreSolicitante: String?,     // JOIN: Usuario.nombre + apellidoPaterno
    val mensaje: String?,
    val estadoSolicitud: String,        // PENDIENTE | APROBADA | RECHAZADA
    val fechaSolicitud: String?
)