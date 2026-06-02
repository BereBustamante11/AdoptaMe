package mx.edu.unpa.adoptame

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.databinding.ActivityRegistrarBinding
import mx.edu.unpa.adoptame.model.request.RegistrarRequest
import mx.edu.unpa.adoptame.repository.AuthRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result

class RegistrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarBinding
    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnCrear.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        val nombre          = binding.txtNombre.text.toString().trim()
        val apellidoPaterno = binding.txtApellidoPaterno.text.toString().trim()
        val email           = binding.txtCorreo.text.toString().trim()
        val password        = binding.txtContrasenna.text.toString().trim()
        val passwordConfirm = binding.txtContrasenna2.text.toString().trim()
        val apellidoMaterno = binding.txtApellidoMaterno.text.toString().trim().takeIf { it.isNotEmpty() }
        val telefono        = binding.txtTelefono.text.toString().trim().takeIf { it.isNotEmpty() }

        if (!validateForm(nombre, apellidoPaterno, email, password, passwordConfirm)) return

        setLoadingState(true)

        val request = RegistrarRequest(
            nombre          = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            email           = email,
            password        = password,
            telefono        = telefono
        )

        lifecycleScope.launch {
            when (val result = authRepository.registrar(request)) {
                is Result.Success -> {
                    // ── CAMBIO: result.data es Usuario directamente ──────────
                    sessionManager.saveSession(result.data)
                    // Ir directo al panel sin pasar por login
                    val intent = Intent(this@RegistrarActivity, PanelPrincipalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    Toast.makeText(this@RegistrarActivity, result.message, Toast.LENGTH_LONG).show()
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun validateForm(
        nombre: String, apellidoPaterno: String,
        email: String, password: String, passwordConfirm: String
    ): Boolean {
        binding.txtNombre.error          = null
        binding.txtApellidoPaterno.error = null
        binding.txtCorreo.error          = null
        binding.txtContrasenna.error     = null
        binding.txtContrasenna2.error    = null

        if (nombre.isEmpty()) {
            binding.txtNombre.error = "El nombre es requerido"
            binding.txtNombre.requestFocus(); return false
        }
        if (apellidoPaterno.isEmpty()) {
            binding.txtApellidoPaterno.error = "El apellido paterno es requerido"
            binding.txtApellidoPaterno.requestFocus(); return false
        }
        // Reemplaza tu bloque de validación de email actual por este:
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            binding.txtCorreo.error = "Ingresa un correo @gmail.com válido"
            binding.txtCorreo.requestFocus()
            return false
        }
        if (password.length < 6) {
            binding.txtContrasenna.error = "Mínimo 6 caracteres"
            binding.txtContrasenna.requestFocus(); return false
        }
        if (password != passwordConfirm) {
            binding.txtContrasenna2.error = "Las contraseñas no coinciden"
            binding.txtContrasenna2.requestFocus(); return false
        }
        return true
    }

    private fun setLoadingState(loading: Boolean) {
        binding.btnCrear.isEnabled               = !loading
        binding.btnCrear.text                    = if (loading) "Creando cuenta..." else "Crear cuenta"
        binding.txtNombre.isEnabled              = !loading
        binding.txtApellidoPaterno.isEnabled     = !loading
        binding.txtApellidoMaterno.isEnabled = !loading
        binding.txtTelefono.isEnabled        = !loading
        binding.txtCorreo.isEnabled              = !loading
        binding.txtContrasenna.isEnabled         = !loading
        binding.txtContrasenna2.isEnabled        = !loading
    }
}
