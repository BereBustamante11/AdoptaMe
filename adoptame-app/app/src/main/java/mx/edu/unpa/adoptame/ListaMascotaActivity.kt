package mx.edu.unpa.adoptame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.adapter.MascotaListaAdapter
import mx.edu.unpa.adoptame.databinding.ActivityListaMascotaBinding
import mx.edu.unpa.adoptame.model.Mascota
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.repository.MascotaRepository
import mx.edu.unpa.adoptame.util.Result

class ListaMascotasActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_TIPO     = "extra_id_tipo"
        const val EXTRA_NOMBRE_TIPO = "extra_nombre_tipo"

        /**
         * Mapa estático de idTipoMascota → drawable local.
         * Debe mantenerse sincronizado con los IDs que devuelve el backend
         * (los mismos que están en CatTipoMascota: 1=Perro, 2=Gato, 3=Ave, 4=Roedor).
         */
        private val PLACEHOLDER_POR_TIPO: Map<Int, Int> = mapOf(
            1 to R.drawable.ic_imagen_perrito_foreground,
            2 to R.drawable.ic_imagen_gatito_foreground,
            3 to R.drawable.ic_imagen_loro_foreground,
            4 to R.drawable.ic_imagen_hamster_foreground
        )
    }

    private lateinit var binding: ActivityListaMascotaBinding
    private lateinit var adapter: MascotaListaAdapter

    private val mascotaRepository = MascotaRepository()
    private val apiService: ApiService =
        RetrofitClient.instance.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idTipo     = intent.getIntExtra(EXTRA_ID_TIPO, -1)
        val nombreTipo = intent.getStringExtra(EXTRA_NOMBRE_TIPO) ?: "Mascotas"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = nombreTipo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (idTipo == -1) {
            Toast.makeText(this, "Error: tipo de mascota inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // El placeholder local correcto según el tipo seleccionado
        val placeholder = PLACEHOLDER_POR_TIPO[idTipo]
            ?: R.drawable.ic_imagen_perrito_foreground

        cargarMascotas(idTipo, nombreTipo, placeholder)
    }

    private fun cargarMascotas(idTipo: Int, nombreTipo: String, placeholder: Int) {
        binding.progressBar.visibility    = View.VISIBLE
        binding.recyclerMascotas.visibility = View.GONE
        binding.txtVacio.visibility       = View.GONE

        lifecycleScope.launch {
            when (val result = mascotaRepository.getMascotasDisponibles(idTipo)) {
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@ListaMascotasActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE

                    if (result.data.isEmpty()) {
                        binding.txtVacio.visibility = View.VISIBLE
                        return@launch
                    }

                    mostrarLista(result.data, nombreTipo, placeholder)

                    // Carga imágenes reales en paralelo tras mostrar la lista
                    cargarImagenesParalelo(result.data)
                }

                Result.Loading -> Unit
            }
        }
    }

    private fun mostrarLista(mascotas: List<Mascota>, nombreTipo: String, placeholder: Int) {
        adapter = MascotaListaAdapter(
            mascotas      = mascotas,
            nombreTipo    = nombreTipo,
            placeholderLocal = placeholder,
            onItemClick   = { mascota ->
                val intent = Intent(this, DetalleMascotaActivity::class.java)
                intent.putExtra(DetalleMascotaActivity.EXTRA_ID_MASCOTA, mascota.idMascota)
                startActivity(intent)
            }
        )

        binding.recyclerMascotas.layoutManager = LinearLayoutManager(this)
        binding.recyclerMascotas.adapter       = adapter
        binding.recyclerMascotas.visibility    = View.VISIBLE
    }

    /**
     * Lanza una coroutine por mascota en paralelo para obtener su imagen principal.
     * Cuando todas terminan, inyecta el mapa al adapter de una sola vez.
     *
     * Si el backend no tiene imágenes aún (USE_MOCK = true en el repo),
     * el catch silencia el error y el placeholder local ya visible permanece.
     */
    private suspend fun cargarImagenesParalelo(mascotas: List<Mascota>) {
        try {
            coroutineScope {
                val trabajos = mascotas.map { mascota ->
                    async {
                        try {
                            val resp = apiService.getImagenesMascota(mascota.idMascota)
                            if (resp.isSuccessful && !resp.body().isNullOrEmpty()) {
                                val imagenes = resp.body()!!
                                val principal = imagenes.firstOrNull { it.imagenPrincipal }
                                    ?: imagenes.first()
                                mascota.idMascota to principal.urlImagen
                            } else {
                                null
                            }
                        } catch (_: Exception) {
                            null   // Sin conexión o error puntual → placeholder se queda
                        }
                    }
                }

                val resultados = trabajos.awaitAll()
                    .filterNotNull()
                    .toMap()

                if (resultados.isNotEmpty()) {
                    adapter.setImagenes(resultados)
                }
            }
        } catch (_: Exception) {
            // coroutineScope puede lanzar si se cancela la Activity — se ignora
        }
    }
}