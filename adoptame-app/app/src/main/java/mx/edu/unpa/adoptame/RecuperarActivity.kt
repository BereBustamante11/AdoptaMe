package mx.edu.unpa.adoptame

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mx.edu.unpa.adoptame.databinding.ActivityRecuperarBinding

/**
 * NOTA: El backend no tiene endpoint de recuperación de contraseña.
 * Esta pantalla informa al usuario y mantiene el flujo de navegación intacto.
 * Si en el futuro se agrega el endpoint, solo cambia el setOnClickListener.
 */
class RecuperarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnActualizar.setOnClickListener {
            val email = binding.txtCorreo.text.toString().trim()

            if (email.isEmpty()) {
                binding.txtCorreo.error = "Ingresa tu correo"
                binding.txtCorreo.requestFocus()
                return@setOnClickListener
            }

            // El backend no tiene este endpoint todavía
            Toast.makeText(
                this,
                "Funcionalidad disponible próximamente. Contacta al administrador.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

