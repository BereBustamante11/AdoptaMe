package mx.edu.unpa.adoptame

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.GaleriaImagenesAdapter
import mx.edu.unpa.adoptame.databinding.ActivityDetalleMascotaBinding
import mx.edu.unpa.adoptame.model.estadoLabel
import mx.edu.unpa.adoptame.model.sexoLabel
import mx.edu.unpa.adoptame.model.request.SolicitudRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.repository.MascotaRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result

class DetalleMascotaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_MASCOTA = "extra_id_mascota"
    }

    private lateinit var binding: ActivityDetalleMascotaBinding
    private val mascotaRepository = MascotaRepository()
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
    private lateinit var sessionManager: SessionManager

    // idMascota activo para la solicitud
    private var idMascotaActual: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        idMascotaActual = intent.getIntExtra(EXTRA_ID_MASCOTA, -1)

        if (idMascotaActual == -1) {
            Toast.makeText(this, "Error al cargar la mascota", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDetalleMascota()
        configurarBotonSolicitud()
    }

    private fun cargarDetalleMascota() {
        binding.progressBar.visibility    = View.VISIBLE
        binding.contenidoDetalle.visibility = View.GONE

        lifecycleScope.launch {
            // ── 1. Cargar datos de la mascota ─────────────────────────────────
            val mascotaResult = try {
                val resp = apiService.getMascotaById(idMascotaActual)
                if (resp.isSuccessful && resp.body() != null)
                    Result.Success(resp.body()!!)
                else
                // Si el backend aún no está, usamos el mock local
                    MascotaRepository.mockMascotas
                        .find { it.idMascota == idMascotaActual }
                        ?.let { Result.Success(it) }
                        ?: Result.Error("Mascota no encontrada")
            } catch (e: Exception) {
                // Sin conexión: buscamos en mock
                MascotaRepository.mockMascotas
                    .find { it.idMascota == idMascotaActual }
                    ?.let { Result.Success(it) }
                    ?: Result.Error(e.message ?: "Error de conexión")
            }

            when (mascotaResult) {
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DetalleMascotaActivity,
                        mascotaResult.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Success -> {
                    val mascota = mascotaResult.data

                    // ── 2. Poblar UI ──────────────────────────────────────────
                    supportActionBar?.title = mascota.nombre
                    binding.txtNombre.text         = mascota.nombre
                    binding.txtRaza.text           = mascota.raza ?: "Sin definir"
                    binding.txtSexo.text           = mascota.sexoLabel
                    binding.txtEdad.text           = mascota.edadAproximada ?: "Desconocida"
                    binding.txtDescripcion.text    = mascota.descripcion ?: "Sin descripción"
                    binding.txtEstado.text         = mascota.estadoLabel

                    val colorRes = when (mascota.estadoAdopcion) {
                        "DISPONIBLE" -> R.color.status_disponible
                        "PROCESO"    -> R.color.status_proceso
                        else         -> R.color.status_adoptado
                    }
                    binding.txtEstado.setTextColor(getColor(colorRes))

                    // Ocultar botón si ya está en proceso o adoptado
                    binding.btnSolicitarAdopcion.isEnabled =
                        mascota.estadoAdopcion == "DISPONIBLE"

                    // ── 3. Cargar imágenes ────────────────────────────────────
                    cargarImagenes(idMascotaActual)

                    binding.progressBar.visibility     = View.GONE
                    binding.contenidoDetalle.visibility = View.VISIBLE
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun cargarImagenes(idMascota: Int) {
        val baseUrl = "http://192.168.1.75:8181"
        lifecycleScope.launch {
            try {
                val resp = apiService.getImagenesMascota(idMascota)
                if (resp.isSuccessful && !resp.body().isNullOrEmpty()) {
                    val imagenes = resp.body()!!

                    // Imagen principal en el ImageView grande
                    val principal = imagenes.firstOrNull { it.imagenPrincipal } ?: imagenes.first()
                    val urlCompletaPrincipal = baseUrl + principal.urlImagen
                    Glide.with(this@DetalleMascotaActivity)
                        .load(urlCompletaPrincipal)
                        .placeholder(R.drawable.ic_logo_adoptame_foreground)
                        .error(R.drawable.ic_logo_adoptame_foreground)
                        .centerCrop()
                        .into(binding.imgPrincipal)

                    // Galería horizontal con todas las fotos
                    if (imagenes.size > 1) {
                        binding.recyclerGaleria.visibility = View.VISIBLE
                        binding.recyclerGaleria.layoutManager = LinearLayoutManager(
                            this@DetalleMascotaActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recyclerGaleria.adapter = GaleriaImagenesAdapter(imagenes) { urlSeleccionada ->
                            Glide.with(this@DetalleMascotaActivity)
                                .load(urlSeleccionada)
                                .centerCrop()
                                .into(binding.imgPrincipal)
                        }
                    }
                }
                // Si no hay imágenes del backend o falla, el placeholder local se queda
            } catch (_: Exception) {
                // Sin conexión o backend sin imágenes → se mantiene el placeholder
            }
        }
    }

    private fun configurarBotonSolicitud() {
        binding.btnSolicitarAdopcion.setOnClickListener {
            val idUsuario = sessionManager.getUserId()

            // Edge case: sesión expirada
            if (idUsuario == -1) {
                Toast.makeText(this, "Debes iniciar sesión para solicitar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mostrarDialogoSolicitud(idUsuario)
        }
    }

    private fun mostrarDialogoSolicitud(idUsuario: Int) {
        // Diálogo simple con un campo de mensaje
        val editText = android.widget.EditText(this).apply {
            hint = "¿Por qué deseas adoptar a esta mascota?"
            setPadding(48, 24, 48, 24)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Solicitar adopción")
            .setView(editText)
            .setPositiveButton("Enviar solicitud") { _, _ ->
                val mensaje = editText.text.toString().trim()
                enviarSolicitud(idUsuario, mensaje.ifEmpty { null })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarSolicitud(idUsuario: Int, mensaje: String?) {
        binding.btnSolicitarAdopcion.isEnabled = false
        binding.btnSolicitarAdopcion.text      = "Enviando..."

        val request = SolicitudRequest(
            idMascota            = idMascotaActual,
            idUsuarioSolicitante = idUsuario,
            mensaje              = mensaje
        )

        lifecycleScope.launch {
            when (val result = mascotaRepository.crearSolicitud(request)) {
                is Result.Success -> {
                    Toast.makeText(
                        this@DetalleMascotaActivity,
                        "✅ Solicitud enviada con éxito. Te contactaremos pronto.",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnSolicitarAdopcion.text = "Solicitud enviada"
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@DetalleMascotaActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSolicitarAdopcion.isEnabled = true
                    binding.btnSolicitarAdopcion.text      = "Solicitar adopción"
                }
                Result.Loading -> Unit
            }
        }
    }
}