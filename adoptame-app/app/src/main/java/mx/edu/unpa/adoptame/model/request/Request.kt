package mx.edu.unpa.adoptame.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegistrarRequest(
    @SerializedName("nombre")           val nombre: String,
    @SerializedName("apellidoPaterno")  val apellidoPaterno: String,
    @SerializedName("apellidoMaterno")  val apellidoMaterno: String?,
    @SerializedName("email")            val email: String,
    @SerializedName("password")         val password: String,
    @SerializedName("telefono")         val telefono: String?
)

// ── ELIMINADO: RecuperarRequest ───────────────────────────────────────────────
// El backend no tiene endpoint de recuperación de contraseña.

data class RegistrarMascotaRequest(
    // ── AÑADIDO: idUsuarioDonador (requerido por MascotaRequestDTO) ───────────
    @SerializedName("idUsuarioDonador") val idUsuarioDonador: Int,
    @SerializedName("idTipoMascota")    val idTipoMascota: Int,
    @SerializedName("nombre")           val nombre: String,
    @SerializedName("raza")             val raza: String?,
    @SerializedName("sexo")             val sexo: String,          // "MACHO" | "HEMBRA"
    @SerializedName("edadAproximada")   val edadAproximada: String?,
    @SerializedName("descripcion")      val descripcion: String?
)

data class SolicitudRequest(
    @SerializedName("idMascota")             val idMascota: Int,
    // ── AÑADIDO: idUsuarioSolicitante (requerido por SolicitudAdopcionRequestDTO) ─
    @SerializedName("idUsuarioSolicitante")  val idUsuarioSolicitante: Int,
    @SerializedName("mensaje")               val mensaje: String?
)
