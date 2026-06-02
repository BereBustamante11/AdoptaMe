package mx.edu.unpa.adoptame.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("idUsuario")        val idUsuario: Int,
    @SerializedName("nombre")           val nombre: String,
    @SerializedName("apellidoPaterno")  val apellidoPaterno: String,
    @SerializedName("apellidoMaterno")  val apellidoMaterno: String?,
    @SerializedName("email")            val email: String,
    @SerializedName("telefono")         val telefono: String?,
    @SerializedName("activo")           val activo: Boolean,
    @SerializedName("fechaRegistro")    val fechaRegistro: String
) {
    val nombreCompleto: String
        get() = "$nombre $apellidoPaterno ${apellidoMaterno.orEmpty()}".trim()
}