package mx.edu.unpa.adoptame.model.response

data class AdopcionResponse(
    val idAdopcion: Int,
    val idSolicitud: Int,
    val idMascota: Int,
    val idUsuarioAdoptante: Int,
    val fechaAdopcion: String?,
    val observaciones: String?
)