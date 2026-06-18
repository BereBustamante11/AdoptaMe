package mx.edu.unpa.adoptame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.adoptame.R
import mx.edu.unpa.adoptame.databinding.ItemMiSolicitudBinding
import mx.edu.unpa.adoptame.model.SolicitudAdopcion

class MisSolicitudesAdapter(
    private val solicitudes: List<SolicitudAdopcion>
) : RecyclerView.Adapter<MisSolicitudesAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemMiSolicitudBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(solicitud: SolicitudAdopcion) {
            binding.txtNombreMascota.text =
                solicitud.nombreMascota ?: "Mascota #${solicitud.idMascota}"

            binding.txtMensaje.text =
                solicitud.mensaje?.takeIf { it.isNotBlank() } ?: "Sin mensaje adjunto."

            binding.txtFechaSolicitud.text = formatearFecha(solicitud.fechaSolicitud)

            configurarChipEstado(solicitud.estadoSolicitud)
            configurarBanners(solicitud.estadoSolicitud)
        }

        private fun configurarChipEstado(estado: String) {
            val (etiqueta, colorRes) = when (estado) {
                "PENDIENTE" -> "Pendiente"  to R.color.status_proceso
                "APROBADA"  -> "Aprobada"   to R.color.status_disponible
                "RECHAZADA" -> "Rechazada"  to R.color.status_adoptado
                else        -> estado       to R.color.text_secondary
            }
            binding.txtEstado.text = etiqueta
            binding.txtEstado.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    binding.root.context.getColor(colorRes)
                )
        }

        private fun configurarBanners(estado: String) {
            binding.bannerAprobada.visibility =
                if (estado == "APROBADA") View.VISIBLE else View.GONE
            binding.bannerRechazada.visibility =
                if (estado == "RECHAZADA") View.VISIBLE else View.GONE
        }

        private fun formatearFecha(fechaIso: String?): String {
            if (fechaIso.isNullOrBlank()) return ""
            return try { fechaIso.substring(0, 10) } catch (_: Exception) { fechaIso }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMiSolicitudBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(solicitudes[position])
    }

    override fun getItemCount(): Int = solicitudes.size
}
