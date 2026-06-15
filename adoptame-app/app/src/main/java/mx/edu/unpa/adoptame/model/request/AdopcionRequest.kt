package mx.edu.unpa.adoptame.model.request

data class AdopcionRequest(
    val idSolicitud: Int,
    val idMascota: Int,
    val idUsuarioAdoptante: Int,
    val observaciones: String? = null
)