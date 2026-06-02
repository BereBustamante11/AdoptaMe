package mx.edu.unpa.adoptame.model

import com.google.gson.annotations.SerializedName

data class TipoMascota(
    @SerializedName("idTipoMascota") val idTipoMascota: Int,
    @SerializedName("descripcion")   val descripcion: String,
    @SerializedName("activo")        val activo: Boolean = true
) {
    // Para mostrar en Spinner sin código extra en el Adapter
    override fun toString(): String = descripcion
}