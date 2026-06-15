package mx.edu.unpa.adoptame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.edu.unpa.adoptame.R
import mx.edu.unpa.adoptame.databinding.ItemSolicitudRecibidaBinding
import mx.edu.unpa.adoptame.model.SolicitudAdopcion

class GestionSolicitudesAdapter(
    private val solicitudes: MutableList<SolicitudAdopcion>,
    private val onAprobar: (solicitud: SolicitudAdopcion, position: Int) -> Unit,
    private val onRechazar: (solicitud: SolicitudAdopcion, position: Int) -> Unit
) : RecyclerView.Adapter<GestionSolicitudesAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemSolicitudRecibidaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(solicitud: SolicitudAdopcion, position: Int) {
            binding.txtNombreMascota.text =
                solicitud.nombreMascota ?: "Mascota #${solicitud.idMascota}"

            binding.txtNombreSolicitante.text =
                solicitud.nombreSolicitante ?: "Usuario #${solicitud.idUsuarioSolicitante}"

            binding.txtMensaje.text =
                solicitud.mensaje?.takeIf { it.isNotBlank() } ?: "Sin mensaje adjunto."

            binding.txtFechaSolicitud.text = formatearFecha(solicitud.fechaSolicitud)

            configurarChipEstado(solicitud.estadoSolicitud)

            // Los botones solo son relevantes cuando la solicitud está pendiente.
            // Una solicitud ya resuelta es solo informativa.
            binding.layoutAcciones.visibility =
                if (solicitud.estadoSolicitud == "PENDIENTE") View.VISIBLE else View.GONE

            binding.btnAprobar.setOnClickListener { onAprobar(solicitud, position) }
            binding.btnRechazar.setOnClickListener { onRechazar(solicitud, position) }
        }

        private fun configurarChipEstado(estado: String) {
            val (etiqueta, colorRes) = when (estado) {
                "PENDIENTE" -> "Pendiente"   to R.color.status_proceso
                "APROBADA"  -> "Aprobada"    to R.color.status_disponible
                "RECHAZADA" -> "Rechazada"   to R.color.status_adoptado
                else        -> estado        to R.color.text_secondary
            }
            binding.txtEstado.text = etiqueta
            binding.txtEstado.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    binding.root.context.getColor(colorRes)
                )
        }

        /**
         * El backend devuelve la fecha en ISO-8601 ("2024-06-15T10:30:00").
         * Mostramos solo la parte de fecha para no sobrecargar la UI.
         */
        private fun formatearFecha(fechaIso: String?): String {
            if (fechaIso.isNullOrBlank()) return ""
            return try {
                fechaIso.substring(0, 10) // "2024-06-15"
            } catch (_: Exception) {
                fechaIso
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSolicitudRecibidaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(solicitudes[position], position)
    }

    override fun getItemCount(): Int = solicitudes.size

    /**
     * Actualiza el estado de una solicitud en el lugar (sin recargar toda la lista).
     * Deshabilita también los botones para evitar doble-tap.
     */
    fun actualizarEstado(position: Int, nuevoEstado: String) {
        val vieja = solicitudes[position]
        solicitudes[position] = vieja.copy(estadoSolicitud = nuevoEstado)
        notifyItemChanged(position)
    }
}