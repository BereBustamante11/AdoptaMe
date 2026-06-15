package mx.edu.unpa.adoptame

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.GestionSolicitudesAdapter
import mx.edu.unpa.adoptame.databinding.ActivityGestionSolicitudesBinding
import mx.edu.unpa.adoptame.model.SolicitudAdopcion
import mx.edu.unpa.adoptame.repository.AdopcionRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result

class GestionSolicitudesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionSolicitudesBinding
    private lateinit var adapter: GestionSolicitudesAdapter
    private lateinit var sessionManager: SessionManager

    private val adopcionRepository = AdopcionRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionSolicitudesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Solicitudes recibidas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val idUsuario = sessionManager.getUserId()

        // Edge case: la sesión podría haber expirado si el usuario llega aquí
        // desde una notificación push o deep link.
        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no válida. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarSolicitudes(idUsuario)
    }

    private fun cargarSolicitudes(idUsuario: Int) {
        mostrarCargando(true)

        lifecycleScope.launch {
            when (val result = adopcionRepository.getSolicitudesRecibidas(idUsuario)) {
                is Result.Error -> {
                    mostrarCargando(false)
                    Toast.makeText(
                        this@GestionSolicitudesActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Result.Success -> {
                    mostrarCargando(false)

                    if (result.data.isEmpty()) {
                        binding.txtVacio.visibility = View.VISIBLE
                        return@launch
                    }

                    configurarRecycler(result.data.toMutableList())
                }

                Result.Loading -> Unit
            }
        }
    }

    private fun configurarRecycler(solicitudes: MutableList<SolicitudAdopcion>) {
        adapter = GestionSolicitudesAdapter(
            solicitudes = solicitudes,
            onAprobar   = { solicitud, position -> confirmarAprobacion(solicitud, position) },
            onRechazar  = { solicitud, position -> confirmarRechazo(solicitud, position) }
        )

        binding.recyclerSolicitudes.layoutManager = LinearLayoutManager(this)
        binding.recyclerSolicitudes.adapter       = adapter
        binding.recyclerSolicitudes.visibility    = View.VISIBLE
    }

    // ── Diálogos de confirmación ──────────────────────────────────────────────

    private fun confirmarAprobacion(solicitud: SolicitudAdopcion, position: Int) {
        val nombre = solicitud.nombreSolicitante ?: "este usuario"
        val mascota = solicitud.nombreMascota ?: "la mascota"

        AlertDialog.Builder(this)
            .setTitle("Aprobar adopción")
            .setMessage(
                "¿Confirmas que $nombre adoptará a $mascota?\n\n" +
                        "La mascota se marcará como adoptada y no recibirá más solicitudes."
            )
            .setPositiveButton("Sí, aprobar") { _, _ ->
                ejecutarAprobacion(solicitud, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarRechazo(solicitud: SolicitudAdopcion, position: Int) {
        val nombre = solicitud.nombreSolicitante ?: "este usuario"

        AlertDialog.Builder(this)
            .setTitle("Rechazar solicitud")
            .setMessage("¿Estás seguro de que quieres rechazar la solicitud de $nombre?")
            .setPositiveButton("Sí, rechazar") { _, _ ->
                ejecutarRechazo(solicitud, position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ── Operaciones de red ────────────────────────────────────────────────────

    private fun ejecutarAprobacion(solicitud: SolicitudAdopcion, position: Int) {
        lifecycleScope.launch {
            when (val result = adopcionRepository.concretarAdopcion(
                idSolicitud        = solicitud.idSolicitud,
                idMascota          = solicitud.idMascota,
                idUsuarioAdoptante = solicitud.idUsuarioSolicitante
            )) {
                is Result.Success -> {
                    adapter.actualizarEstado(position, "APROBADA")
                    Toast.makeText(
                        this@GestionSolicitudesActivity,
                        "✅ ¡Adopción concretada! ${solicitud.nombreMascota ?: "La mascota"} ya tiene hogar.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@GestionSolicitudesActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Result.Loading -> Unit
            }
        }
    }

    private fun ejecutarRechazo(solicitud: SolicitudAdopcion, position: Int) {
        lifecycleScope.launch {
            when (val result = adopcionRepository.rechazarSolicitud(solicitud.idSolicitud)) {
                is Result.Success -> {
                    adapter.actualizarEstado(position, "RECHAZADA")
                    Toast.makeText(
                        this@GestionSolicitudesActivity,
                        "Solicitud rechazada.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Result.Error -> {
                    Toast.makeText(
                        this@GestionSolicitudesActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Result.Loading -> Unit
            }
        }
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private fun mostrarCargando(cargando: Boolean) {
        binding.progressBar.visibility     = if (cargando) View.VISIBLE else View.GONE
        binding.recyclerSolicitudes.visibility = if (cargando) View.GONE else binding.recyclerSolicitudes.visibility
    }
}