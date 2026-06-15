package mx.edu.unpa.adoptame.repository

import mx.edu.unpa.adoptame.model.Usuario
import mx.edu.unpa.adoptame.model.request.ActualizarPerfilRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.util.Result

class UsuarioRepository {

    private val apiService: ApiService =
        RetrofitClient.instance.create(ApiService::class.java)

    /**
     * GET /api/usuarios/{id}
     * Carga los datos completos del usuario (nombre, apellidos, teléfono, urlFotoPerfil).
     */
    suspend fun getUsuario(idUsuario: Int): Result<Usuario> {
        return try {
            val response = apiService.getUsuario(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No se pudo obtener el perfil (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualiza nombre, apellidos, teléfono y urlFotoPerfil.
     * El backend debe retornar el Usuario actualizado completo.
     */
    suspend fun actualizarPerfil(idUsuario: Int, request: ActualizarPerfilRequest): Result<Usuario> {
        return try {
            val response = apiService.actualizarPerfil(idUsuario, request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("No se pudo guardar el perfil (${response.code()})")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión")
        }
    }
}