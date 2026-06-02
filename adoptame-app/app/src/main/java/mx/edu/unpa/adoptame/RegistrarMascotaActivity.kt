package mx.edu.unpa.adoptame

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.databinding.MascotaRegistrarBinding
import mx.edu.unpa.adoptame.model.SexoMascota
import mx.edu.unpa.adoptame.model.TipoMascota
import mx.edu.unpa.adoptame.model.request.RegistrarMascotaRequest
import mx.edu.unpa.adoptame.network.ApiService
import mx.edu.unpa.adoptame.network.RetrofitClient
import mx.edu.unpa.adoptame.repository.MascotaRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarMascotaActivity : AppCompatActivity() {

    private lateinit var binding: MascotaRegistrarBinding
    private val mascotaRepository = MascotaRepository()
    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)
    private lateinit var sessionManager: SessionManager

    private var tiposDisponibles: List<TipoMascota> = emptyList()

    // URI de la foto tomada con cámara (se necesita para FileProvider)
    private var uriCamaraActual: Uri? = null

    // Lista de URIs seleccionadas (máximo 5 fotos, la primera es la principal)
    private val fotosSeleccionadas: MutableList<Uri> = mutableListOf()

    // ── Launchers de permisos y resultados ───────────────────────────────────

    private val permisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara() else mostrarMensajePermiso()
    }

    private val camaraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exitoso ->
        if (exitoso && uriCamaraActual != null) {
            agregarFoto(uriCamaraActual!!)
        }
    }

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) agregarFoto(uri)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MascotaRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Registrar mascota"
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        cargarTiposMascota()
        configurarBotones()
    }

    private fun cargarTiposMascota() {
        lifecycleScope.launch {
            when (val result = mascotaRepository.getTiposMascota()) {
                is Result.Success -> {
                    tiposDisponibles = result.data
                    val adapter = ArrayAdapter(
                        this@RegistrarMascotaActivity,
                        android.R.layout.simple_spinner_item,
                        tiposDisponibles
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.idSpinnerTipoAnimal.adapter = adapter
                }
                is Result.Error   -> Toast.makeText(this@RegistrarMascotaActivity, result.message, Toast.LENGTH_SHORT).show()
                Result.Loading    -> Unit
            }
        }
    }

    private fun configurarBotones() {
        binding.btnAgregarFoto.setOnClickListener { mostrarOpcionFoto() }
        binding.idBtnRegistrarAnimal.setOnClickListener { intentarRegistrar() }
    }

    // ── Manejo de fotos ───────────────────────────────────────────────────────

    private fun mostrarOpcionFoto() {
        // Edge case: máximo 5 fotos
        if (fotosSeleccionadas.size >= 5) {
            Toast.makeText(this, "Máximo 5 fotos permitidas", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Agregar foto")
            .setItems(arrayOf("Tomar foto con cámara", "Elegir de galería")) { _, opcion ->
                when (opcion) {
                    0 -> solicitarPermisoCamara()
                    1 -> galeriaLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun solicitarPermisoCamara() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> abrirCamara()
            else -> permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        val archivoFoto = crearArchivoTemporal()
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            archivoFoto
        )
        uriCamaraActual = uri // Guardamos la referencia para usarla después
        camaraLauncher.launch(uri)
    }

    private fun crearArchivoTemporal(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("MASCOTA_${timestamp}_", ".jpg", dir)
    }

    private fun agregarFoto(uri: Uri) {
        fotosSeleccionadas.add(uri)
        actualizarVistaFotos()
    }

    private fun actualizarVistaFotos() {
        val cantidad = fotosSeleccionadas.size
        binding.txtConteoFotos.text = when (cantidad) {
            0    -> "Sin fotos agregadas"
            1    -> "1 foto agregada (principal)"
            else -> "$cantidad fotos agregadas"
        }
        binding.txtConteoFotos.visibility = View.VISIBLE
    }

    private fun mostrarMensajePermiso() {
        Toast.makeText(
            this,
            "Se necesita permiso de cámara. Puedes habilitarlo en Configuración.",
            Toast.LENGTH_LONG
        ).show()
    }

    // ── Registro ──────────────────────────────────────────────────────────────

    private fun intentarRegistrar() {
        val nombre      = binding.idNombreAnimal.text.toString().trim()
        val raza        = binding.idRazaAnimal.text.toString().trim()
        val edad        = binding.idEdadAnimal.text.toString().trim()
        val descripcion = binding.idDescripcionAnimal.text.toString().trim()

        // Edge cases de validación
        if (nombre.isEmpty()) {
            binding.idNombreAnimal.error = "El nombre es requerido"
            binding.idNombreAnimal.requestFocus()
            return
        }
        if (tiposDisponibles.isEmpty()) {
            Toast.makeText(this, "Espera a que carguen los tipos de mascota", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoSeleccionado = tiposDisponibles[binding.idSpinnerTipoAnimal.selectedItemPosition]
        val sexo = if (binding.idRadioMacho.isChecked) SexoMascota.MACHO.valor else SexoMascota.HEMBRA.valor
        val idDonador = sessionManager.getUserId()

        if (idDonador == -1) {
            Toast.makeText(this, "Error de sesión. Inicia sesión de nuevo.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RegistrarMascotaRequest(
            idUsuarioDonador = idDonador,
            idTipoMascota    = tipoSeleccionado.idTipoMascota,
            nombre           = nombre,
            raza             = raza.ifEmpty { null },
            sexo             = sexo,
            edadAproximada   = edad.ifEmpty { null },
            descripcion      = descripcion.ifEmpty { null }
        )

        setLoadingState(true)

        lifecycleScope.launch {
            when (val result = mascotaRepository.registrarMascota(request)) {
                is Result.Success -> {
                    val mascotaCreada = result.data

                    // Si hay fotos, subirlas al backend
                    if (fotosSeleccionadas.isNotEmpty()) {
                        subirFotos(mascotaCreada.idMascota)
                    } else {
                        mostrarExito()
                    }
                }
                is Result.Error -> {
                    setLoadingState(false)
                    Toast.makeText(
                        this@RegistrarMascotaActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
                Result.Loading -> Unit
            }
        }
    }

    /**
     * Sube cada foto al backend via POST /api/imagenes-mascota (multipart).
     * La primera foto se marca como imagenPrincipal = 1.
     *
     * INSTRUCCIONES BACKEND:
     * El endpoint POST /api/imagenes-mascota debe aceptar:
     *   - @RequestParam idMascota: Int
     *   - @RequestParam imagenPrincipal: Int (0 o 1)
     *   - @RequestPart archivo: MultipartFile
     * Y retornar ImagenMascotaResponseDTO.
     */
    private suspend fun subirFotos(idMascota: Int) {
        var todasSubidas = true

        fotosSeleccionadas.forEachIndexed { index, uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return@forEachIndexed
                val byteArray   = inputStream.readBytes()
                inputStream.close()

                val idMascotaBody = idMascota.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val esPrincipal = if (index == 0) "1" else "0"
                val esPrincipalBody = esPrincipal
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                val archivoBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val archivoPart = MultipartBody.Part.createFormData(
                    "archivo", "foto_${index}.jpg", archivoBody
                )

                val response = apiService.subirImagen(idMascotaBody, esPrincipalBody, archivoPart)
                if (!response.isSuccessful) todasSubidas = false

            } catch (e: Exception) {
                todasSubidas = false
            }
        }

        if (todasSubidas) {
            mostrarExito()
        } else {
            setLoadingState(false)
            // La mascota se creó, pero algunas fotos fallaron — no bloqueamos al usuario
            Toast.makeText(
                this,
                "Mascota registrada, pero algunas fotos no se subieron. Intenta de nuevo.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun mostrarExito() {
        Toast.makeText(this, "✅ Mascota registrada exitosamente", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun setLoadingState(loading: Boolean) {
        binding.idBtnRegistrarAnimal.isEnabled = !loading
        binding.idBtnRegistrarAnimal.text      = if (loading) "Registrando..." else "Registrar"
        binding.btnAgregarFoto.isEnabled       = !loading
    }
}