package mx.edu.unpa.adoptame.repository

import mx.edu.unpa.adoptame.model.*
import mx.edu.unpa.adoptame.model.request.RegistrarMascotaRequest
import mx.edu.unpa.adoptame.model.request.SolicitudRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.util.Result

/**
 * Sin parámetro token: el backend no requiere autenticación.
 */
class MascotaRepository {

    // ── ELIMINADO: constructor(token: String) ────────────────────────────────
    private val apiService: ApiService =
        RetrofitClient.instance.create(ApiService::class.java)

    private val USE_MOCK = false

    suspend fun getTiposMascota(): Result<List<TipoMascota>> {
        if (USE_MOCK) return Result.Success(mockTipos)

        return try {
            val response = apiService.getTiposMascota()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No se pudieron cargar los tipos de mascota")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    /**
     * El backend solo tiene GET /mascotas/disponibles (sin filtro por tipo).
     * El filtro por [idTipo] se aplica en el cliente.
     * Si idTipo == null, retorna todas las disponibles.
     */
    suspend fun getMascotasDisponibles(idTipo: Int? = null): Result<List<Mascota>> {
        if (USE_MOCK) {
            val resultado = if (idTipo != null)
                mockMascotas.filter { it.idTipoMascota == idTipo }
            else
                mockMascotas
            return Result.Success(resultado)
        }

        return try {
            val response = apiService.getMascotasDisponibles()
            if (response.isSuccessful && response.body() != null) {
                val lista = response.body()!!
                val filtrada = if (idTipo != null) lista.filter { it.idTipoMascota == idTipo } else lista
                Result.Success(filtrada)
            } else {
                Result.Error("No se pudieron cargar las mascotas")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun registrarMascota(request: RegistrarMascotaRequest): Result<Mascota> {
        if (USE_MOCK) {
            return Result.Success(
                Mascota(
                    idMascota        = (100..999).random(),
                    idUsuarioDonador = request.idUsuarioDonador,
                    idTipoMascota    = request.idTipoMascota,
                    nombre           = request.nombre,
                    raza             = request.raza,
                    sexo             = request.sexo,
                    edadAproximada   = request.edadAproximada,
                    descripcion      = request.descripcion,
                    estadoAdopcion   = "DISPONIBLE",
                    activo           = true,
                    fechaPublicacion = null
                )
            )
        }

        return try {
            val response = apiService.registrarMascota(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No se pudo registrar la mascota")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun crearSolicitud(request: SolicitudRequest): Result<SolicitudAdopcion> {
        if (USE_MOCK) {
            return Result.Success(
                SolicitudAdopcion(
                    idSolicitud          = (100..999).random(),
                    idMascota            = request.idMascota,
                    idUsuarioSolicitante = request.idUsuarioSolicitante,
                    mensaje              = request.mensaje,
                    estadoSolicitud      = "PENDIENTE",
                    fechaSolicitud       = null
                )
            )
        }

        return try {
            val response = apiService.crearSolicitud(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No se pudo enviar la solicitud")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    // ─── Datos mock ───────────────────────────────────────────────────────────
    companion object {
        val mockTipos = listOf(
            TipoMascota(1, "Perro"),
            TipoMascota(2, "Gato"),
            TipoMascota(3, "Ave"),
            TipoMascota(4, "Roedor")
        )

        val mockMascotas = listOf(
            Mascota(1, 1, 1, "Firulais", "Corgi",    "MACHO",  "2 años",   "Perrito muy amigable.", "DISPONIBLE", true, null),
            Mascota(2, 2, 2, "Michi",    "Siamés",   "HEMBRA", "6 meses",  "Gatita tranquila.",     "DISPONIBLE", true, null),
            Mascota(3, 1, 1, "Rocky",    "P. Alemán","MACHO",  "1 año",    "Activo y protector.",   "DISPONIBLE", true, null),
            Mascota(4, 2, 3, "Piolín",   "Canario",  "MACHO",  "1 año",    "Canta todo el día.",    "DISPONIBLE", true, null)
        )
    }
}
