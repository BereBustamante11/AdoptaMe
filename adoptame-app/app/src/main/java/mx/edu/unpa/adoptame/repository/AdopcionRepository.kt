package mx.edu.unpa.adoptame.repository

import mx.edu.unpa.adoptame.model.SolicitudAdopcion
import mx.edu.unpa.adoptame.model.request.AdopcionRequest
import mx.edu.unpa.adoptame.model.request.EstadoSolicitudRequest
import mx.edu.unpa.adoptame.model.response.AdopcionResponse
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.util.Result

class AdopcionRepository {

    private val apiService: ApiService =
        RetrofitClient.instance.create(ApiService::class.java)

    /**
     * Carga todas las solicitudes que recibió el donador para sus mascotas.
     */
    suspend fun getSolicitudesRecibidas(idUsuario: Int): Result<List<SolicitudAdopcion>> {
        return try {
            val resp = apiService.getSolicitudesRecibidas(idUsuario)
            when {
                resp.isSuccessful && resp.body() != null -> Result.Success(resp.body()!!)
                resp.code() == 404                       -> Result.Success(emptyList())
                else -> Result.Error("Error al cargar solicitudes (${resp.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    /**
     * Aprueba una solicitud y concreta la adopción en una sola llamada.
     * El backend es responsable de la transacción completa:
     *   crear Adopcion + marcar Solicitud APROBADA + marcar Mascota ADOPTADA.
     */
    suspend fun concretarAdopcion(
        idSolicitud: Int,
        idMascota: Int,
        idUsuarioAdoptante: Int
    ): Result<AdopcionResponse> {
        return try {
            val resp = apiService.registrarAdopcion(
                AdopcionRequest(
                    idSolicitud       = idSolicitud,
                    idMascota         = idMascota,
                    idUsuarioAdoptante = idUsuarioAdoptante
                )
            )
            if (resp.isSuccessful && resp.body() != null) {
                Result.Success(resp.body()!!)
            } else {
                Result.Error("No se pudo completar la adopción (${resp.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    /**
     * Rechaza una solicitud de adopción.
     */
    suspend fun rechazarSolicitud(idSolicitud: Int): Result<SolicitudAdopcion> {
        return try {
            val resp = apiService.cambiarEstadoSolicitud(
                idSolicitud,
                EstadoSolicitudRequest("RECHAZADA")
            )
            if (resp.isSuccessful && resp.body() != null) {
                Result.Success(resp.body()!!)
            } else {
                Result.Error("No se pudo rechazar la solicitud (${resp.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }
}