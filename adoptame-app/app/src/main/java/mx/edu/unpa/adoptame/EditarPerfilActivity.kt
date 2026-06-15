package mx.edu.unpa.adoptame

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.databinding.ActivityEditarPerfilBinding
import mx.edu.unpa.adoptame.model.request.ActualizarPerfilRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.NetworkConfig
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.repository.UsuarioRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.FotoPickerHelper
import mx.edu.unpa.adoptame.util.Result
import mx.edu.unpa.adoptame.util.UrlHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var fotoPicker: FotoPickerHelper

    // null = el usuario no cambió la foto en esta sesión
    private var nuevoUriLocal: Uri? = null

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        fotoPicker = FotoPickerHelper(this) { uri ->
            nuevoUriLocal = uri
            cargarImagenEnAvatar(uri)
        }

        configurarToolbar()
        cargarDatosDesdePerfil()
        configurarBotones()
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // ── Carga inicial de datos ────────────────────────────────────────────────

    /**
     * GET /api/usuarios/{id} — carga los campos individuales desde el backend
     * para poder pre-llenar nombre, apellidos y teléfono correctamente.
     * El email lo mostramos directo desde caché (no cambia).
     */
    private fun cargarDatosDesdePerfil() {
        binding.txtEmailReadOnly.text = sessionManager.getUserEmail()
        // UrlHelper convierte el path relativo guardado en caché a URL absoluta
        cargarImagenDesdeUrl(sessionManager.getUserFotoUrl())

        setLoadingState(true)
        lifecycleScope.launch {
            val idUsuario = sessionManager.getUserId()
            when (val result = UsuarioRepository().getUsuario(idUsuario)) {
                is Result.Success -> {
                    val u = result.data
                    binding.txtNombre.setText(u.nombre)
                    binding.txtApellidoPaterno.setText(u.apellidoPaterno)
                    binding.txtApellidoMaterno.setText(u.apellidoMaterno.orEmpty())
                    binding.txtTelefono.setText(u.telefono.orEmpty())
                    // u.urlFotoPerfil es el path relativo del back → UrlHelper lo hace absoluto
                    cargarImagenDesdeUrl(u.urlFotoPerfil)
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@EditarPerfilActivity,
                        "No se pudo cargar el perfil actual",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Result.Loading -> Unit
            }
            setLoadingState(false)
        }
    }

    // ── Foto de perfil ────────────────────────────────────────────────────────

    private fun mostrarOpcionesFoto() {
        AlertDialog.Builder(this)
            .setTitle("Cambiar foto de perfil")
            .setItems(arrayOf("Seleccionar de galería", "Tomar foto")) { _, opcion ->
                when (opcion) {
                    0 -> fotoPicker.lanzarGaleria()
                    1 -> fotoPicker.lanzarCamara()
                }
            }
            .show()
    }

    private fun cargarImagenEnAvatar(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_avatar_placeholder)
            .into(binding.imgFotoPerfil)
    }

    private fun cargarImagenDesdeUrl(path: String?) {
        // UrlHelper maneja null, blank y URLs ya absolutas — Glide solo ve null o URL válida
        Glide.with(this)
            .load(UrlHelper.fotoPerfilUrl(path))
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_avatar_placeholder)
            .error(R.drawable.ic_avatar_placeholder)
            .into(binding.imgFotoPerfil)
    }

    // ── Botones ───────────────────────────────────────────────────────────────

    private fun configurarBotones() {
        binding.fabCambiarFoto.setOnClickListener { mostrarOpcionesFoto() }
        binding.imgFotoPerfil.setOnClickListener  { mostrarOpcionesFoto() }
        binding.btnGuardar.setOnClickListener     { intentarGuardar() }
    }

    // ── Validación y guardado ─────────────────────────────────────────────────

    private fun intentarGuardar() {
        val nombre    = binding.txtNombre.text.toString().trim()
        val apPaterno = binding.txtApellidoPaterno.text.toString().trim()
        val apMaterno = binding.txtApellidoMaterno.text.toString().trim().ifBlank { null }
        val telefono  = binding.txtTelefono.text.toString().trim().ifBlank { null }

        if (nombre.isBlank()) {
            binding.txtNombre.error = "El nombre es requerido"
            binding.txtNombre.requestFocus()
            return
        }
        if (apPaterno.isBlank()) {
            binding.txtApellidoPaterno.error = "El apellido paterno es requerido"
            binding.txtApellidoPaterno.requestFocus()
            return
        }
        if (telefono != null && telefono.length < 10) {
            binding.txtTelefono.error = "Ingresa al menos 10 dígitos"
            binding.txtTelefono.requestFocus()
            return
        }

        setLoadingState(true)
        lifecycleScope.launch {
            val idUsuario = sessionManager.getUserId()

            // Paso 1: subir foto si cambió. Si falla, conservamos la URL anterior
            // en lugar de guardar null y borrar la foto del usuario.
            val urlFotoFinal: String? = if (nuevoUriLocal != null) {
                subirFotoPerfil(idUsuario, nuevoUriLocal!!)
                    ?: sessionManager.getUserFotoUrl()   // ← fallback: no pierde la foto anterior
            } else {
                sessionManager.getUserFotoUrl()
            }

            // Paso 2: actualizar datos del perfil
            val request = ActualizarPerfilRequest(
                nombre          = nombre,
                apellidoPaterno = apPaterno,
                apellidoMaterno = apMaterno,
                telefono        = telefono,
                urlFotoPerfil   = urlFotoFinal
            )

            when (val result = UsuarioRepository().actualizarPerfil(idUsuario, request)) {
                is Result.Success -> {
                    val u = result.data
                    sessionManager.updatePerfil(
                        nombre = u.nombre,
                        apellidoPaterno = u.apellidoPaterno,
                        apellidoMaterno = u.apellidoMaterno,
                        telefono       = u.telefono,
                        urlFotoPerfil  = u.urlFotoPerfil   // path relativo — UrlHelper lo hace absoluto cuando se necesite
                    )
                    Toast.makeText(this@EditarPerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    Toast.makeText(this@EditarPerfilActivity, result.message, Toast.LENGTH_LONG).show()
                }
                Result.Loading -> Unit
            }
        }
    }

    // ── Subida de foto al backend ─────────────────────────────────────────────

    /**
     * POST /api/usuarios/{id}/foto
     *
     * ── Fix del Uri de cámara: ────────────────────────────────────────────
     *   Con galería, el Uri apunta a MediaStore → contentResolver lo abre bien.
     *   Con cámara, el Uri es un FileProvider Uri apuntando a filesDir.
     *   Ambos se abren igual con contentResolver.openInputStream() — el fix
     *   real está en FotoPickerHelper.crearArchivoTemporal() que ahora usa
     *   filesDir en lugar de externalFilesDir.
     *
     * ── Por qué no tragamos la excepción silenciosamente: ─────────────────
     *   Un catch vacío hace que urlFotoFinal = null, el PUT guarda null en BD
     *   y borra la foto anterior sin ningún mensaje al usuario. Ahora logueamos
     *   el error y retornamos la URL anterior para no perder datos.
     */
    private suspend fun subirFotoPerfil(idUsuario: Int, uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
                ?: run {
                    Log.e("EditarPerfil", "No se pudo abrir el Uri: $uri")
                    return null
                }

            val byteArray = inputStream.readBytes()
            inputStream.close()

            if (byteArray.isEmpty()) {
                Log.e("EditarPerfil", "El archivo de foto está vacío. Uri: $uri")
                return null
            }

            val archivoPart = MultipartBody.Part.createFormData(
                "archivo",
                "perfil_${idUsuario}.jpg",
                byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
            )

            val response = apiService.subirFotoPerfil(idUsuario, archivoPart)

            if (response.isSuccessful) {
                response.body()?.urlFotoPerfil
            } else {
                Log.e("EditarPerfil", "Error al subir foto: HTTP ${response.code()}")
                null
            }

        } catch (e: Exception) {
            Log.e("EditarPerfil", "Excepción al subir foto: ${e.message}", e)
            null
        }
    }

    // ── Estado de carga ───────────────────────────────────────────────────────

    private fun setLoadingState(loading: Boolean) {
        binding.btnGuardar.isEnabled  = !loading
        binding.btnGuardar.text       = if (loading) "Guardando..." else "Guardar cambios"
        binding.fabCambiarFoto.isEnabled = !loading
        binding.progressBar.visibility   = if (loading) View.VISIBLE else View.GONE
    }
}