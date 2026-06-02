package mx.edu.unpa.adoptame.network

import mx.edu.unpa.adoptame.model.ImagenMascota
import mx.edu.unpa.adoptame.model.Mascota
import mx.edu.unpa.adoptame.model.SolicitudAdopcion
import mx.edu.unpa.adoptame.model.TipoMascota
import mx.edu.unpa.adoptame.model.Usuario
import mx.edu.unpa.adoptame.model.request.LoginRequest
import mx.edu.unpa.adoptame.model.request.RegistrarMascotaRequest
import mx.edu.unpa.adoptame.model.request.RegistrarRequest
import mx.edu.unpa.adoptame.model.request.SolicitudRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Declara todos los endpoints.
 * Los paths son suposiciones lógicas — ajústalos cuando tengas la documentación real.
 *
 * NOTA: Todos los métodos son `suspend` para usarse con coroutines.
 */
interface ApiService {

    // ─── /api/usuarios ────────────────────────────────────────────────────────

    /** POST /api/usuarios/registro → UsuarioResponseDTO */
    @POST("usuarios/registro")
    suspend fun registrar(
        @Body request: RegistrarRequest
    ): Response<Usuario>

    /** POST /api/usuarios/login → UsuarioResponseDTO (sin token) */
    @POST("usuarios/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<Usuario>

    // ─── /api/tipos-mascota ───────────────────────────────────────────────────

    /** GET /api/tipos-mascota → List<CatTipoMascotaResponseDTO> */
    @GET("tipos-mascota")
    suspend fun getTiposMascota(): Response<List<TipoMascota>>

    // ─── /api/mascotas ────────────────────────────────────────────────────────

    /**
     * GET /api/mascotas/disponibles → List<MascotaResponseDTO>
     * Retorna TODAS las disponibles. El filtro por tipo se hace en el cliente.
     */
    @GET("mascotas/disponibles")
    suspend fun getMascotasDisponibles(): Response<List<Mascota>>

    /** GET /api/mascotas/{id} → MascotaResponseDTO */
    @GET("mascotas/{id}")
    suspend fun getMascotaById(
        @Path("id") idMascota: Int
    ): Response<Mascota>

    /** POST /api/mascotas → MascotaResponseDTO */
    @POST("mascotas")
    suspend fun registrarMascota(
        @Body request: RegistrarMascotaRequest
    ): Response<Mascota>

    // ─── /api/imagenes-mascota ────────────────────────────────────────────────

    /**
     * GET /api/imagenes-mascota/mascota/{idMascota} → List<ImagenMascotaResponseDTO>
     */
    @GET("imagenes-mascota/mascota/{idMascota}")
    suspend fun getImagenesMascota(
        @Path("idMascota") idMascota: Int
    ): Response<List<ImagenMascota>>

    /**
     * POST /api/imagenes-mascota → multipart/form-data
     * Params: idMascota, imagenPrincipal (opcional), archivo (MultipartFile)
     */
    @Multipart
    @POST("imagenes-mascota")
    suspend fun subirImagen(
        @Part("idMascota") idMascota: RequestBody,
        @Part("imagenPrincipal") imagenPrincipal: RequestBody?,
        @Part archivo: MultipartBody.Part
    ): Response<ImagenMascota>

    // ─── /api/solicitudes ────────────────────────────────────────────────────

    /** POST /api/solicitudes → SolicitudAdopcionResponseDTO */
    @POST("solicitudes")
    suspend fun crearSolicitud(
        @Body request: SolicitudRequest
    ): Response<SolicitudAdopcion>

    /** GET /api/solicitudes/usuario/{idUsuario} */
    @GET("solicitudes/usuario/{idUsuario}")
    suspend fun getMisSolicitudes(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<SolicitudAdopcion>>
}

