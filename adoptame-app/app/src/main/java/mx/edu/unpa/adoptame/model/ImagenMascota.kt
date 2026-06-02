package mx.edu.unpa.adoptame.model

import com.google.gson.annotations.SerializedName

data class ImagenMascota(
    @SerializedName("idImagen")         val idImagen: Int,
    @SerializedName("idMascota")        val idMascota: Int,
    @SerializedName("urlImagen")        val urlImagen: String,
    @SerializedName("imagenPrincipal")  val imagenPrincipal: Boolean,
    @SerializedName("fechaSubida")      val fechaSubida: String
)