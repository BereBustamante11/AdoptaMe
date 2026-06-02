package mx.edu.unpa.adoptame.repository

import mx.edu.unpa.adoptame.model.Usuario
import mx.edu.unpa.adoptame.model.request.LoginRequest
import mx.edu.unpa.adoptame.model.request.RegistrarRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.util.Result

class AuthRepository {

    private val apiService: ApiService =
        RetrofitClient.instance.create(ApiService::class.java)

    // Cambiar a false cuando el servidor esté corriendo
    private val USE_MOCK = false

    suspend fun login(request: LoginRequest): Result<Usuario> {
        if (USE_MOCK) return mockLogin(request)

        return try {
            val response = apiService.login(request)
            // Backend retorna el UsuarioDTO directamente (HTTP 200) o lanza excepción (401/404)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Correo o contraseña incorrectos")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión. Verifica tu red.")
        }
    }

    suspend fun registrar(request: RegistrarRequest): Result<Usuario> {
        if (USE_MOCK) return mockRegistrar(request)

        return try {
            val response = apiService.registrar(request)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // HTTP 409 = email ya registrado es el caso más común
                val msg = if (response.code() == 409) "Este correo ya está registrado"
                else "No se pudo crear la cuenta (${response.code()})"
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de conexión.")
        }
    }

    // ── ELIMINADO: recuperarContrasenna() ─────────────────────────────────────
    // El backend no tiene endpoint de recuperación de contraseña.
    // RecuperarActivity mostrará un mensaje informativo al usuario.

    // ─── Mocks ────────────────────────────────────────────────────────────────
    private fun mockLogin(request: LoginRequest): Result<Usuario> {
        return if (request.email == "test@adoptame.com" && request.password == "123456") {
            Result.Success(
                Usuario(
                    idUsuario       = 1,
                    nombre          = "Usuario",
                    apellidoPaterno = "Test",
                    apellidoMaterno = null,
                    email           = request.email,
                    telefono        = null,
                    activo          = true,
                    fechaRegistro   = "2026-01-01T00:00:00"
                )
            )
        } else {
            Result.Error("Correo o contraseña incorrectos")
        }
    }

    private fun mockRegistrar(request: RegistrarRequest): Result<Usuario> {
        return if (request.email.contains("@") && request.password.length >= 6) {
            Result.Success(
                Usuario(
                    idUsuario       = 99,
                    nombre          = request.nombre,
                    apellidoPaterno = request.apellidoPaterno,
                    apellidoMaterno = request.apellidoMaterno,
                    email           = request.email,
                    telefono        = request.telefono,
                    activo          = true,
                    fechaRegistro   = "2026-01-01T00:00:00"
                )
            )
        } else {
            Result.Error("Datos inválidos para el registro")
        }
    }
}
