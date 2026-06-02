package mx.edu.unpa.adoptame.model

import com.google.gson.annotations.SerializedName

/**
 * Mapeado 1:1 con MascotaResponseDTO del backend.
 *
 * CAMBIO respecto a la versión anterior:
 * - tipoMascota: TipoMascota  →  idTipoMascota: Int
 *   (el backend no tiene relaciones → devuelve solo el ID)
 * - sexo: SexoMascota enum    →  sexo: String ("MACHO" / "HEMBRA")
 * - estadoAdopcion: EstadoAdopcion enum → estadoAdopcion: String
 * - eliminado imagenes: List<ImagenMascota> (se obtienen por endpoint separado)
 *
 * Los enums se mantienen como helpers de UI (ver propiedades de extensión abajo).
 */
data class Mascota(
    @SerializedName("idMascota")        val idMascota: Int,
    @SerializedName("idUsuarioDonador") val idUsuarioDonador: Int,
    @SerializedName("idTipoMascota")    val idTipoMascota: Int,
    @SerializedName("nombre")           val nombre: String,
    @SerializedName("raza")             val raza: String?,
    @SerializedName("sexo")             val sexo: String,           // "MACHO" | "HEMBRA"
    @SerializedName("edadAproximada")   val edadAproximada: String?,
    @SerializedName("descripcion")      val descripcion: String?,
    @SerializedName("estadoAdopcion")   val estadoAdopcion: String, // "DISPONIBLE" | "PROCESO" | "ADOPTADO"
    @SerializedName("activo")           val activo: Boolean = true,
    @SerializedName("fechaPublicacion") val fechaPublicacion: String?
)

// ── Helpers de UI (no afectan la serialización) ───────────────────────────────

/** Texto legible para mostrar en pantalla */
val Mascota.sexoLabel: String
    get() = if (sexo == "MACHO") "Macho" else "Hembra"

val Mascota.estadoLabel: String
    get() = when (estadoAdopcion) {
        "DISPONIBLE" -> "En adopción"
        "PROCESO"    -> "En proceso"
        "ADOPTADO"   -> "Adoptado"
        else         -> estadoAdopcion
    }

// ── Enums auxiliares (para Spinner de Sexo en el form de registro) ─────────────
enum class SexoMascota(val valor: String, val etiqueta: String) {
    MACHO("MACHO", "Macho"),
    HEMBRA("HEMBRA", "Hembra");

    override fun toString() = etiqueta
    companion object { fun opciones() = listOf(MACHO, HEMBRA) }
}
