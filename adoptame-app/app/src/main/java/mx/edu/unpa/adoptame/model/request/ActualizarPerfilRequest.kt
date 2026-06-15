package mx.edu.unpa.adoptame.model.request

import com.google.gson.annotations.SerializedName

/**
 * DTO para PUT /api/usuarios/{id}.
 * Solo los campos editables por el usuario — email e idUsuario no se envían.
 * urlFotoPerfil es la URL pública devuelta por el endpoint de subida de foto.
 */
data class ActualizarPerfilRequest(
    @SerializedName("nombre")           val nombre: String,
    @SerializedName("apellidoPaterno")  val apellidoPaterno: String,
    @SerializedName("apellidoMaterno")  val apellidoMaterno: String?,
    @SerializedName("telefono")         val telefono: String?,
    @SerializedName("urlFotoPerfil")    val urlFotoPerfil: String?
)