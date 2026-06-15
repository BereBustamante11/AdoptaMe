package mx.edu.unpa.adoptame.session

import android.content.Context
import mx.edu.unpa.adoptame.model.Usuario

/**
 * Sin JWT: el backend retorna directamente el UsuarioResponseDTO en el login.
 * Guardamos los datos clave del usuario en SharedPreferences para:
 *   1. Saber quién está logueado (idUsuario).
 *   2. Pre-llenar EditarPerfilActivity sin llamada de red extra.
 *   3. Mostrar avatar en el Toolbar sin llamada de red extra.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME       = "adoptame_session"
        private const val KEY_USER_ID      = "user_id"
        private const val KEY_EMAIL        = "user_email"
        private const val KEY_NOMBRE       = "user_nombre"
        private const val KEY_APELLIDO_PATERNO       = "user_apellido_paterno"
        private const val KEY_APELLIDO_MATERNO       = "user_apellido_materno"
        private const val KEY_TELEFONO     = "user_telefono"      // ← NUEVO
        private const val KEY_FOTO_URL     = "user_foto_url"      // ← NUEVO
        private const val NO_SESSION       = -1
    }

    /** Guarda los datos del usuario tras un login/registro exitoso. */
    fun saveSession(usuario: Usuario) {
        prefs.edit()
            .putInt(KEY_USER_ID,   usuario.idUsuario)
            .putString(KEY_EMAIL,  usuario.email)
            .putString(KEY_NOMBRE, usuario.nombre)
            .putString(KEY_APELLIDO_PATERNO,usuario.apellidoPaterno)
            .putString(KEY_APELLIDO_MATERNO,usuario.apellidoMaterno)
            .putString(KEY_TELEFONO, usuario.telefono)            // ← NUEVO
            .putString(KEY_FOTO_URL,  usuario.urlFotoPerfil)      // ← NUEVO
            .apply()
    }

    /**
     * Actualiza solo los campos que el usuario puede editar en EditarPerfilActivity.
     * No toca idUsuario ni email (son inmutables desde el cliente).
     *
     * Llama a esto después de que el backend confirme el PUT /api/usuarios/{id}.
     */
    fun updatePerfil(
        nombre: String,
        apellidoPaterno:String,
        apellidoMaterno: String?,
        telefono: String?,
        urlFotoPerfil: String?
    ) {
        prefs.edit()
            .putString(KEY_NOMBRE,   nombre)
            .putString(KEY_APELLIDO_PATERNO,apellidoPaterno)
            .putString(KEY_APELLIDO_MATERNO,apellidoMaterno)
            .putString(KEY_TELEFONO, telefono)
            .putString(KEY_FOTO_URL, urlFotoPerfil)
            .apply()
    }

    fun getUserId(): Int          = prefs.getInt(KEY_USER_ID, NO_SESSION)
    fun getUserEmail(): String?   = prefs.getString(KEY_EMAIL, null)
    fun getUserNombre(): String?  = prefs.getString(KEY_NOMBRE, null)
    fun getUserApellidoPaterno(): String? =prefs.getString(KEY_APELLIDO_PATERNO,null)
    fun getUserApellidoMaterno(): String? =prefs.getString(KEY_APELLIDO_MATERNO,null)
    fun getUserTelefono(): String? = prefs.getString(KEY_TELEFONO, null)   // ← NUEVO
    fun getUserFotoUrl(): String?  = prefs.getString(KEY_FOTO_URL, null)   // ← NUEVO

    /** Hay sesión activa si tenemos un idUsuario válido. */
    fun isLoggedIn(): Boolean = getUserId() != NO_SESSION

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}