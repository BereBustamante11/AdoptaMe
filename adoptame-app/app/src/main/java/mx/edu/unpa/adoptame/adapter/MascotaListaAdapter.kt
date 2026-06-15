package mx.edu.unpa.adoptame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.adoptame.R
import mx.edu.unpa.adoptame.databinding.ItemMascotaListaBinding
import mx.edu.unpa.adoptame.model.Mascota
import mx.edu.unpa.adoptame.model.estadoLabel
import mx.edu.unpa.adoptame.model.sexoLabel
import mx.edu.unpa.adoptame.network.NetworkConfig

/**
 * Adapter para el listado de mascotas por categoría.
 *
 * Recibe:
 *  - [mascotas]      Lista filtrada por tipo (ya viene del repo)
 *  - [nombreTipo]    Nombre legible del tipo ("Gato", "Perro"…) para mostrarlo en el chip
 *  - [placeholderLocal] Drawable local que corresponde al tipo seleccionado
 *  - [onItemClick]   Callback al tocar la card o el botón
 *
 * Las imágenes reales se inyectan con [setImagenes] una vez que
 * ListaMascotasActivity recibe el resultado de getImagenesMascota.
 */
class MascotaListaAdapter(
    private val mascotas: List<Mascota>,
    private val nombreTipo: String,
    private val placeholderLocal: Int,          // R.drawable.ic_imagen_gatito_foreground, etc.
    private val onItemClick: (Mascota) -> Unit
) : RecyclerView.Adapter<MascotaListaAdapter.ViewHolder>() {

    // idMascota → URL de imagen principal. Se rellena tras cargar imágenes del backend.
    private val imagenesMap: MutableMap<Int, String> = mutableMapOf()

    /**
     * Llamado desde ListaMascotasActivity una vez que obtiene las URLs del backend.
     * Solo notifica los items cuya imagen cambió.
     */
    fun setImagenes(mapa: Map<Int, String>) {
        val keysNuevas = mapa.keys
        imagenesMap.putAll(mapa)
        mascotas.forEachIndexed { index, mascota ->
            if (mascota.idMascota in keysNuevas) notifyItemChanged(index)
        }
    }

    inner class ViewHolder(
        private val binding: ItemMascotaListaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mascota: Mascota) {
            // ── Texto ─────────────────────────────────────────────────────────
            binding.txtNombreMascota.text = mascota.nombre
            binding.txtTipoMascota.text   = nombreTipo          // "Gato", "Perro"…  ← CORREGIDO
            binding.txtRaza.text          = mascota.raza ?: "Sin definir"
            binding.txtSexo.text          = mascota.sexoLabel
            binding.txtEstado.text        = mascota.estadoLabel  // "En adopción", "En proceso"…

            // Color del chip de tipo según estado
            val colorRes = when (mascota.estadoAdopcion) {
                "DISPONIBLE" -> R.color.status_disponible
                "PROCESO"    -> R.color.status_proceso
                else         -> R.color.status_adoptado
            }
            binding.txtTipoMascota.setTextColor(binding.root.context.getColor(colorRes))

            // ── Imagen ────────────────────────────────────────────────────────
            val urlBackend = imagenesMap[mascota.idMascota]
            if (!urlBackend.isNullOrBlank()) {

                // 1. Define tu IP (Cambia esta IP por la IP real de tu computadora en tu Wi-Fi)
                val baseUrl = NetworkConfig.BASE_URL

                // 2. Une la IP con la ruta que mandó el backend
                val urlCompleta = baseUrl + urlBackend

                // 3. Usa la URL completa en Glide
                Glide.with(binding.imgMascota)
                    .load(urlCompleta)
                    .placeholder(placeholderLocal)
                    .error(placeholderLocal)
                    .centerCrop()
                    .into(binding.imgMascota)
            } else {
                // Sin URL aún → placeholder local del tipo correcto
                binding.imgMascota.setImageResource(placeholderLocal)
            }

            // ── Clicks ────────────────────────────────────────────────────────
            binding.btnEnAdopcion.setOnClickListener { onItemClick(mascota) }
            binding.root.setOnClickListener { onItemClick(mascota) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMascotaListaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(mascotas[position])

    override fun getItemCount(): Int = mascotas.size
}