package mx.edu.unpa.adoptame

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.TipoMascotaAdapter
import mx.edu.unpa.adoptame.databinding.ActivityPanelPrincipalBinding
import mx.edu.unpa.adoptame.model.TipoMascota
import mx.edu.unpa.adoptame.repository.MascotaRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result
import mx.edu.unpa.adoptame.util.UrlHelper

class PanelPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelPrincipalBinding
    private val mascotaRepository = MascotaRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        cargarTiposMascota()
    }

    override fun onResume() {
        super.onResume()
        // Recargamos el menú para actualizar el avatar si cambió la foto
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_panel_principal, menu)

        // Avatar circular como acción en el extremo derecho del toolbar
        val itemAvatar = menu.findItem(R.id.action_perfil)
        val avatarView = itemAvatar?.actionView as? ImageView ?: return true

        avatarView.layoutParams = android.view.ViewGroup.LayoutParams(96, 96)
        avatarView.scaleType = ImageView.ScaleType.CENTER_CROP

        Glide.with(this)
            .load(UrlHelper.fotoPerfilUrl(sessionManager.getUserFotoUrl()))
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_avatar_placeholder)
            .error(R.drawable.ic_avatar_placeholder)
            .into(avatarView)

        avatarView.setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_mis_solicitudes_enviadas -> {
                startActivity(Intent(this, MisSolicitudesActivity::class.java))
                true
            }
            R.id.action_mis_solicitudes -> {
                startActivity(Intent(this, GestionSolicitudesActivity::class.java))
                true
            }
            R.id.action_registrar_mascota -> {
                startActivity(Intent(this, RegistrarMascotaActivity::class.java))
                true
            }
            R.id.action_cerrar_sesion -> {
                sessionManager.clearSession()
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ── Tipos de mascota ──────────────────────────────────────────────────────

    private fun cargarTiposMascota() {
        lifecycleScope.launch {
            when (val result = mascotaRepository.getTiposMascota()) {
                is Result.Success -> configurarRecycler(result.data)
                is Result.Error   -> Toast.makeText(
                    this@PanelPrincipalActivity,
                    result.message,
                    Toast.LENGTH_SHORT
                ).show()
                Result.Loading    -> Unit
            }
        }
    }

    private fun configurarRecycler(tipos: List<TipoMascota>) {
        lifecycleScope.launch {
            val conteos = try {
                coroutineScope {
                    tipos.map { tipo ->
                        async {
                            val result = mascotaRepository.getMascotasDisponibles(tipo.idTipoMascota)
                            val count  = if (result is Result.Success) result.data.size else 0
                            tipo.idTipoMascota to count
                        }
                    }.awaitAll().toMap()
                }
            } catch (_: Exception) {
                tipos.associate { it.idTipoMascota to 0 }
            }

            val adapter = TipoMascotaAdapter(tipos, conteos) { tipoSeleccionado ->
                val intent = Intent(this@PanelPrincipalActivity, ListaMascotasActivity::class.java)
                intent.putExtra(ListaMascotasActivity.EXTRA_ID_TIPO, tipoSeleccionado.idTipoMascota)
                intent.putExtra(ListaMascotasActivity.EXTRA_NOMBRE_TIPO, tipoSeleccionado.descripcion)
                startActivity(intent)
            }

            binding.recyclerGridMascota.layoutManager =
                GridLayoutManager(this@PanelPrincipalActivity, 2)
            binding.recyclerGridMascota.adapter = adapter
        }
    }
}
