package mx.edu.unpa.adoptame.util

import mx.edu.unpa.adoptame.network.NetworkConfig

/**
 * Convierte el path relativo que retorna el backend en una URL absoluta
 * que Glide puede cargar directamente.
 *
 * ── Por qué aquí y no en SessionManager: ─────────────────────────────────
 *   SessionManager es una capa de persistencia local — no debe conocer
 *   la dirección del servidor. UrlHelper es el único punto donde se
 *   concatena BASE_URL + path, cumpliendo Single Responsibility.
 *
 * ── Uso: ──────────────────────────────────────────────────────────────────
 *   Glide.with(ctx).load(UrlHelper.fotoPerfilUrl(usuario.urlFotoPerfil))...
 *   Glide.with(ctx).load(UrlHelper.fotoPerfilUrl(sessionManager.getUserFotoUrl()))...
 *
 * ── Edge cases cubiertos: ─────────────────────────────────────────────────
 *   - null / blank → retorna null (Glide muestra el placeholder)
 *   - ya es URL absoluta (empieza con http) → la retorna sin modificar
 *     (útil si el back ya retorna URLs completas en el futuro)
 *   - path con "/" inicial → no duplica el separador
 */
object UrlHelper {

    fun fotoPerfilUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path

        val base = NetworkConfig.BASE_URL.trimEnd('/')
        val relative = path.trimStart('/')
        return "$base/$relative"
    }
}