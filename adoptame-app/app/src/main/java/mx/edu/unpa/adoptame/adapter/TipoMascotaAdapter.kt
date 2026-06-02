package mx.edu.unpa.adoptame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.adoptame.databinding.ActivityPanelPrincipalItemBinding
import mx.edu.unpa.adoptame.model.TipoMascota

/**
 * Adapter para el grid de categorías en PanelPrincipalActivity.
 *
 * @param tipos        Lista de tipos de mascota
 * @param conteos      Mapa idTipoMascota → cantidad disponible
 * @param onItemClick  Callback cuando el usuario toca una categoría
 */
class TipoMascotaAdapter(
    private val tipos: List<TipoMascota>,
    private val conteos: Map<Int, Int>,
    private val onItemClick: (TipoMascota) -> Unit
) : RecyclerView.Adapter<TipoMascotaAdapter.ViewHolder>() {

    // Mapa de imágenes por nombre de tipo (fallback al perro si no existe)
    // Si en el futuro el backend devuelve una URL de imagen, usa Glide aquí.
    private val imagenesLocales: Map<String, Int> by lazy {
        mapOf(
            "Perro"   to mx.edu.unpa.adoptame.R.drawable.ic_imagen_perrito_foreground,
            "Gato"    to mx.edu.unpa.adoptame.R.drawable.ic_imagen_gatito_foreground,
            "Ave"     to mx.edu.unpa.adoptame.R.drawable.ic_imagen_loro_foreground,
            "Roedor"  to mx.edu.unpa.adoptame.R.drawable.ic_imagen_hamster_foreground
        )
    }

    inner class ViewHolder(
        private val binding: ActivityPanelPrincipalItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tipo: TipoMascota) {
            binding.txtTipoMascota.text = tipo.descripcion

            val cantidad = conteos[tipo.idTipoMascota] ?: 0
            binding.txtEstatus.text = if (cantidad > 0) "Disponibles" else "No Disponible"

            // Color verde si hay disponibles, rojo si no
            val colorRes = if (cantidad > 0)
                mx.edu.unpa.adoptame.R.color.status_disponible
            else
                mx.edu.unpa.adoptame.R.color.status_adoptado

            binding.txtEstatus.setTextColor(
                binding.root.context.getColor(colorRes)
            )

            // Label del tipo en morado
            binding.txtUbicacion.text = tipo.descripcion

            // Imagen local según el nombre del tipo
            val imgRes = imagenesLocales[tipo.descripcion]
                ?: mx.edu.unpa.adoptame.R.drawable.ic_imagen_perrito_foreground
            binding.imgMascota.setImageResource(imgRes)

            binding.root.setOnClickListener { onItemClick(tipo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ActivityPanelPrincipalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tipos[position])
    }

    override fun getItemCount(): Int = tipos.size
}