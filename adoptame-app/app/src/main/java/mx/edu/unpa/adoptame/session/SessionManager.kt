package mx.edu.unpa.adoptame.session

import android.content.Context
import mx.edu.unpa.adoptame.model.Usuario

/**
 * Sin JWT: el backend retorna directamente el UsuarioResponseDTO en el login.
 * Guardamos el idUsuario en SharedPreferences para saber quién está logueado.
 */
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME     = "adoptame_session"
        private const val KEY_USER_ID    = "user_id"
        private const val KEY_EMAIL      = "user_email"
        private const val KEY_NOMBRE     = "user_nombre"
        private const val NO_SESSION     = -1
    }

    /** Guarda los datos del usuario tras un login/registro exitoso */
    fun saveSession(usuario: Usuario) {
        prefs.edit()
            .putInt(KEY_USER_ID, usuario.idUsuario)
            .putString(KEY_EMAIL,  usuario.email)
            .putString(KEY_NOMBRE, usuario.nombreCompleto)
            .apply()
    }

    fun getUserId(): Int      = prefs.getInt(KEY_USER_ID, NO_SESSION)
    fun getUserEmail(): String?  = prefs.getString(KEY_EMAIL, null)
    fun getUserNombre(): String? = prefs.getString(KEY_NOMBRE, null)

    /** Hay sesión activa si tenemos un idUsuario válido */
    fun isLoggedIn(): Boolean = getUserId() != NO_SESSION

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
