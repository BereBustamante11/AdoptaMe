package mx.edu.unpa.adoptame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.MisSolicitudesAdapter
import mx.edu.unpa.adoptame.databinding.ActivityMisSolicitudesBinding
import mx.edu.unpa.adoptame.model.SolicitudAdopcion
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.NotificacionHelper
import mx.edu.unpa.adoptame.util.Result

class MisSolicitudesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMisSolicitudesBinding
    private lateinit var sessionManager: SessionManager
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    // Ids de solicitudes aprobadas ya notificadas (evita duplicar notificaciones)
    private val PREFS_NOTIF = "adoptame_notif"
    private val KEY_NOTIFICADAS = "notificadas"

    // Launcher para pedir permiso POST_NOTIFICATIONS (Android 13+)
    private val permisosLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Si el usuario niega, las notificaciones simplemente no aparecen */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisSolicitudesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        NotificacionHelper.crearCanal(this)
        pedirPermisoNotificaciones()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mis solicitudes enviadas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val idUsuario = sessionManager.getUserId()
        if (idUsuario == -1) {
            Toast.makeText(this, "Sesión no válida.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        cargarMisSolicitudes(idUsuario)
    }

    private fun cargarMisSolicitudes(idUsuario: Int) {
        mostrarCargando(true)

        lifecycleScope.launch {
            try {
                val resp = apiService.getMisSolicitudes(idUsuario)
                mostrarCargando(false)

                if (resp.isSuccessful && resp.body() != null) {
                    val lista = resp.body()!!
                    if (lista.isEmpty()) {
                        binding.txtVacio.visibility = View.VISIBLE
                    } else {
                        notificarAprobacionesPendientes(lista)
                        configurarRecycler(lista)
                    }
                } else if (resp.code() == 404) {
                    binding.txtVacio.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        this@MisSolicitudesActivity,
                        "Error al cargar solicitudes (${resp.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                mostrarCargando(false)
                Toast.makeText(
                    this@MisSolicitudesActivity,
                    e.message ?: "Error de conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Dispara una notificación local por cada solicitud APROBADA que aún
     * no haya sido notificada. Usa SharedPreferences para no repetirla.
     */
    private fun notificarAprobacionesPendientes(solicitudes: List<SolicitudAdopcion>) {
        val prefs = getSharedPreferences(PREFS_NOTIF, MODE_PRIVATE)
        val yaNotificadas = prefs.getStringSet(KEY_NOTIFICADAS, emptySet())!!.toMutableSet()

        solicitudes
            .filter { it.estadoSolicitud == "APROBADA" }
            .filter { it.idSolicitud.toString() !in yaNotificadas }
            .forEach { solicitud ->
                val nombreMascota = solicitud.nombreMascota ?: "tu mascota"
                NotificacionHelper.notificarAdopcionAprobada(
                    context       = this,
                    nombreMascota = nombreMascota,
                    notifId       = solicitud.idSolicitud
                )
                yaNotificadas.add(solicitud.idSolicitud.toString())
            }

        prefs.edit().putStringSet(KEY_NOTIFICADAS, yaNotificadas).apply()
    }

    private fun configurarRecycler(solicitudes: List<SolicitudAdopcion>) {
        val adapter = MisSolicitudesAdapter(solicitudes)
        binding.recyclerMisSolicitudes.layoutManager = LinearLayoutManager(this)
        binding.recyclerMisSolicitudes.adapter = adapter
        binding.recyclerMisSolicitudes.visibility = View.VISIBLE
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permisosLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
