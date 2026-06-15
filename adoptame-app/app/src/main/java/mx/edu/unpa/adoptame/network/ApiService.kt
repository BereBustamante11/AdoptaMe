package mx.edu.unpa.adoptame.network

import mx.edu.unpa.adoptame.model.ImagenMascota
import mx.edu.unpa.adoptame.model.Mascota
import mx.edu.unpa.adoptame.model.SolicitudAdopcion
import mx.edu.unpa.adoptame.model.TipoMascota
import mx.edu.unpa.adoptame.model.Usuario
import mx.edu.unpa.adoptame.model.request.ActualizarPerfilRequest
import mx.edu.unpa.adoptame.model.request.AdopcionRequest
import mx.edu.unpa.adoptame.model.request.EstadoSolicitudRequest
import mx.edu.unpa.adoptame.model.request.LoginRequest
import mx.edu.unpa.adoptame.model.request.RegistrarMascotaRequest
import mx.edu.unpa.adoptame.model.request.RegistrarRequest
import mx.edu.unpa.adoptame.model.request.SolicitudRequest
import mx.edu.unpa.adoptame.model.response.AdopcionResponse
import mx.edu.unpa.adoptame.model.response.FotoPerfilResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ─── /api/usuarios ────────────────────────────────────────────────────────

    @POST("usuarios/registro")
    suspend fun registrar(@Body request: RegistrarRequest): Response<Usuario>

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuario>

    // ─── /api/tipos-mascota ───────────────────────────────────────────────────

    @GET("tipos-mascota")
    suspend fun getTiposMascota(): Response<List<TipoMascota>>

    // ─── /api/mascotas ────────────────────────────────────────────────────────

    @GET("mascotas/disponibles")
    suspend fun getMascotasDisponibles(): Response<List<Mascota>>

    @GET("mascotas/{id}")
    suspend fun getMascotaById(@Path("id") idMascota: Int): Response<Mascota>

    @POST("mascotas")
    suspend fun registrarMascota(@Body request: RegistrarMascotaRequest): Response<Mascota>

    // ─── /api/imagenes-mascota ────────────────────────────────────────────────

    @GET("imagenes-mascota/mascota/{idMascota}")
    suspend fun getImagenesMascota(@Path("idMascota") idMascota: Int): Response<List<ImagenMascota>>

    @Multipart
    @POST("imagenes-mascota")
    suspend fun subirImagen(
        @Part("idMascota") idMascota: RequestBody,
        @Part("imagenPrincipal") imagenPrincipal: RequestBody?,
        @Part archivo: MultipartBody.Part
    ): Response<ImagenMascota>

    // ─── /api/solicitudes ────────────────────────────────────────────────────

    /** Crea una nueva solicitud (adoptante → mascota). */
    @POST("solicitudes")
    suspend fun crearSolicitud(@Body request: SolicitudRequest): Response<SolicitudAdopcion>

    /** Solicitudes que YO envié como posible adoptante. */
    @GET("solicitudes/usuario/{idUsuario}")
    suspend fun getMisSolicitudes(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<SolicitudAdopcion>>

    /**
     * Solicitudes RECIBIDAS para las mascotas que yo doné.
     * Endpoint a implementar en el backend:
     *   GET /api/solicitudes/donador/{idUsuario}
     *   → JOIN SolicitudAdopcion ⟵ Mascota WHERE Mascota.idUsuarioDonador = idUsuario
     */
    @GET("solicitudes/donador/{idUsuario}")
    suspend fun getSolicitudesRecibidas(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<SolicitudAdopcion>>

    /**
     * Cambia el estado de una solicitud (PENDIENTE → APROBADA | RECHAZADA).
     * Úsalo directamente solo para RECHAZADA.
     * Para APROBADA, llama a registrarAdopcion que hace la transacción completa.
     */
    @PATCH("solicitudes/{idSolicitud}/estado")
    suspend fun cambiarEstadoSolicitud(
        @Path("idSolicitud") idSolicitud: Int,
        @Body request: EstadoSolicitudRequest
    ): Response<SolicitudAdopcion>

    // ─── /api/adopciones ─────────────────────────────────────────────────────

    /**
     * Concreta la adopción. El backend debe hacer en una sola transacción:
     *  1. Crear registro en Adopcion
     *  2. SolicitudAdopcion.estadoSolicitud = APROBADA
     *  3. Mascota.estadoAdopcion = ADOPTADO
     *  4. (Opcional) Rechazar las demás solicitudes PENDIENTE de esa mascota
     */
    @POST("adopciones")
    suspend fun registrarAdopcion(@Body request: AdopcionRequest): Response<AdopcionResponse>

    @GET("adopciones/usuario/{idUsuario}")
    suspend fun getMisAdopciones(
        @Path("idUsuario") idUsuario: Int
    ): Response<List<AdopcionResponse>>
    /** Obtiene los datos completos de un usuario por ID. */
    @GET("usuarios/{id}")
    suspend fun getUsuario(
        @Path("id") idUsuario: Int
    ): Response<Usuario>

    /** Actualiza nombre, apellidos, teléfono y urlFotoPerfil. */
    @PUT("usuarios/{id}")
    suspend fun actualizarPerfil(
        @Path("id")  idUsuario: Int,
        @Body        request: ActualizarPerfilRequest
    ): Response<Usuario>

    /**
     * Sube la foto de perfil como multipart.
     * El backend guarda el archivo y retorna la URL pública en FotoPerfilResponse.
     *
     * Contrato esperado del backend:
     *   POST /api/usuarios/{id}/foto
     *   @RequestPart archivo: MultipartFile
     *   → { "urlFotoPerfil": "https://..." }
     */
    @Multipart
    @POST("usuarios/{id}/foto")
    suspend fun subirFotoPerfil(
        @Path("id")  idUsuario: Int,
        @Part        archivo: MultipartBody.Part
    ): Response<FotoPerfilResponse>
}