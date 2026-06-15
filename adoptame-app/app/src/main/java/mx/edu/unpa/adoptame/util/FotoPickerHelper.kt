package mx.edu.unpa.adoptame.util

import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.savedstate.SavedStateRegistryOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Encapsula galería + cámara con estado sobreviviente al ciclo de vida.
 *
 * ── El bug que esto resuelve: ─────────────────────────────────────────────
 *   TakePicture lanza la cámara (proceso externo). Android puede matar la
 *   Activity mientras la cámara está activa (memoria baja, rotación).
 *   Cuando la Activity se recrea, uriCamaraTemp era null → el callback
 *   de TakePicture llegaba con exitoso=true pero sin Uri → foto invisible.
 *
 *   Solución: persistimos el Uri en el savedStateRegistry de la Activity,
 *   igual que haría un ViewModel con SavedStateHandle pero sin necesitar
 *   el módulo lifecycle-viewmodel.
 *
 * ── Contrato FileProvider (los tres deben coincidir siempre): ─────────────
 *   1. AndroidManifest  → android:authorities="${applicationId}.provider"
 *   2. Aquí             → "${activity.packageName}.provider"
 *   3. file_paths.xml   → el directorio que uses en crearArchivoTemporal()
 *
 * ── file_paths.xml recomendado (compatible con externalFilesDir): ─────────
 *   <paths>
 *       <external-files-path name="my_images" path="Pictures/" />
 *   </paths>
 *
 *   Esto es lo que ya tenías y funciona en RegistrarMascotaActivity.
 *   NO lo cambies — el problema nunca fue el xml, sino el Uri volátil.
 */
class FotoPickerHelper(
    private val activity: AppCompatActivity,
    private val onImagenSeleccionada: (Uri) -> Unit
) {

    companion object {
        private const val KEY_URI_CAMARA = "foto_picker_uri_camara"
    }

    /**
     * Uri persistido en el savedState de la Activity.
     * Sobrevive rotaciones y recreaciones mientras la cámara está abierta.
     */
    private var uriCamaraTemp: Uri?
        get() = activity.savedStateRegistry
            .consumeRestoredStateForKey(KEY_URI_CAMARA)
            ?.getParcelable(KEY_URI_CAMARA)
            ?: _uriEnMemoria
        set(value) {
            _uriEnMemoria = value
            // Registramos el proveedor de estado solo una vez
            runCatching {
                activity.savedStateRegistry.registerSavedStateProvider(KEY_URI_CAMARA) {
                    android.os.Bundle().apply {
                        putParcelable(KEY_URI_CAMARA, _uriEnMemoria)
                    }
                }
            }
        }

    // Copia en memoria para el caso donde el proveedor ya está registrado
    private var _uriEnMemoria: Uri? = null

    private val launcherGaleria: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImagenSeleccionada(it) }
        }

    private val launcherCamara: ActivityResultLauncher<Uri> =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { exitoso ->
            if (exitoso) {
                // Leemos _uriEnMemoria directamente: si la Activity sobrevivió,
                // está aquí. Si fue recreada, savedStateRegistry ya restauró el valor
                // antes de que este callback se ejecute.
                _uriEnMemoria?.let { onImagenSeleccionada(it) }
            }
        }

    fun lanzarGaleria() {
        launcherGaleria.launch("image/*")
    }

    fun lanzarCamara() {
        val archivo = crearArchivoTemporal() ?: return
        val uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            archivo
        )
        // Persistimos antes de lanzar la cámara
        uriCamaraTemp = uri
        launcherCamara.launch(uri)
    }

    /**
     * Mismo directorio que RegistrarMascotaActivity: getExternalFilesDir(Pictures).
     * Esto coincide con <external-files-path path="Pictures/"> en file_paths.xml.
     *
     * NO usamos createTempFile() — usamos File() con nombre determinístico
     * para evitar que el SO genere un path distinto al que registramos en el Uri.
     */
    private fun crearArchivoTemporal(): File? {
        return try {
            val dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: return null
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            File(dir, "PERFIL_TEMP_$timestamp.jpg").also {
                if (!it.exists()) it.createNewFile()
            }
        } catch (e: Exception) {
            null
        }
    }
}