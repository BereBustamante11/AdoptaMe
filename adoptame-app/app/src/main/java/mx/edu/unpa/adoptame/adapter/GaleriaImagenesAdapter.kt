package mx.edu.unpa.adoptame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mx.edu.unpa.adoptame.R
import mx.edu.unpa.adoptame.databinding.ItemGaleriaImagenBinding
import mx.edu.unpa.adoptame.model.ImagenMascota

/**
 * Adapter para la galería horizontal de fotos en DetalleMascotaActivity.
 * Al tocar un thumbnail, [onImagenClick] actualiza la imagen principal.
 */
class GaleriaImagenesAdapter(
    private val imagenes: List<ImagenMascota>,
    private val onImagenClick: (urlImagen: String) -> Unit
) : RecyclerView.Adapter<GaleriaImagenesAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemGaleriaImagenBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imagen: ImagenMascota) {
            // 1. Define tu IP y arma la URL completa
            val baseUrl = "http://192.168.1.75:8181"
            val urlCompleta = baseUrl + imagen.urlImagen

            // 2. Pásale la urlCompleta a Glide
            Glide.with(binding.imgThumbnail)
                .load(urlCompleta)
                .placeholder(R.drawable.ic_imagen_perrito_foreground)
                .error(R.drawable.ic_imagen_perrito_foreground)
                .centerCrop()
                .into(binding.imgThumbnail)

            // 3. Importante: pasa la urlCompleta en el evento click para que la vista en grande también funcione
            binding.root.setOnClickListener { onImagenClick(urlCompleta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGaleriaImagenBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagenes[position])
    }

    override fun getItemCount(): Int = imagenes.size
}