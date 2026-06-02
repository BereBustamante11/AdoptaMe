package mx.edu.unpa.adoptame

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.TipoMascotaAdapter
import mx.edu.unpa.adoptame.databinding.ActivityPanelPrincipalBinding
import mx.edu.unpa.adoptame.model.TipoMascota
import mx.edu.unpa.adoptame.repository.MascotaRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result

class PanelPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelPrincipalBinding
    private val mascotaRepository = MascotaRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Configura el Toolbar con el logo y los íconos de acción (+, menú)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        cargarTiposMascota()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_panel_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_registrar_mascota -> {
                startActivity(Intent(this, RegistrarMascotaActivity::class.java))
                true
            }
            R.id.action_cerrar_sesion -> {
                sessionManager.clearSession()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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
        // Para cada tipo buscamos cuántos animales disponibles hay (en mock o real)
        lifecycleScope.launch {
            val conteos = tipos.associate { tipo ->
                val resultMascotas = mascotaRepository.getMascotasDisponibles(tipo.idTipoMascota)
                val count = if (resultMascotas is Result.Success) resultMascotas.data.size else 0
                tipo.idTipoMascota to count
            }

            val adapter = TipoMascotaAdapter(tipos, conteos) { tipoSeleccionado ->
                val intent = Intent(this@PanelPrincipalActivity, ListaMascotasActivity::class.java)
                intent.putExtra(ListaMascotasActivity.EXTRA_ID_TIPO, tipoSeleccionado.idTipoMascota)
                intent.putExtra(ListaMascotasActivity.EXTRA_NOMBRE_TIPO, tipoSeleccionado.descripcion)
                startActivity(intent)
            }

            binding.recyclerGridMascota.layoutManager = GridLayoutManager(
                this@PanelPrincipalActivity, 2
            )
            binding.recyclerGridMascota.adapter = adapter
        }
    }
}