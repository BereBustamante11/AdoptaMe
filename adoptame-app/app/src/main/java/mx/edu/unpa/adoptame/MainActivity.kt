package mx.edu.unpa.adoptame

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.edu.unpa.adoptame.databinding.ActivityMainBinding
import mx.edu.unpa.adoptame.model.request.LoginRequest
import mx.edu.unpa.adoptame.repository.AuthRepository
import mx.edu.unpa.adoptame.session.SessionManager
import mx.edu.unpa.adoptame.util.Result

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authRepository = AuthRepository()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            goToPanelPrincipal()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnIniciarSesion.setOnClickListener { attemptLogin() }
        binding.btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistrarActivity::class.java))
        }
        binding.btnRecuperar.setOnClickListener {
            startActivity(Intent(this, RecuperarActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email    = binding.txtCorreo.text.toString().trim()
        val password = binding.txtContrasenna.text.toString().trim()

        if (!validateForm(email, password)) return

        setLoadingState(true)

        lifecycleScope.launch {
            when (val result = authRepository.login(LoginRequest(email, password))) {
                is Result.Success -> {
                    // ── CAMBIO: result.data ahora es Usuario (no LoginResponse) ──
                    sessionManager.saveSession(result.data)
                    goToPanelPrincipal()
                }
                is Result.Error -> {
                    setLoadingState(false)
                    binding.txtCorreo.error = result.message
                }
                Result.Loading -> Unit
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        binding.txtCorreo.error      = null
        binding.txtContrasenna.error = null

        if (email.isEmpty()) {
            binding.txtCorreo.error = "El correo es requerido"
            binding.txtCorreo.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtCorreo.error = "Ingresa un correo válido"
            binding.txtCorreo.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.txtContrasenna.error = "La contraseña es requerida"
            binding.txtContrasenna.requestFocus()
            return false
        }
        if (password.length < 6) {
            binding.txtContrasenna.error = "Mínimo 6 caracteres"
            binding.txtContrasenna.requestFocus()
            return false
        }
        return true
    }

    private fun setLoadingState(loading: Boolean) {
        binding.btnIniciarSesion.isEnabled = !loading
        binding.btnIniciarSesion.text      = if (loading) "Iniciando..." else "Iniciar sesión"
        binding.txtCorreo.isEnabled        = !loading
        binding.txtContrasenna.isEnabled   = !loading
    }

    private fun goToPanelPrincipal() {
        startActivity(Intent(this, PanelPrincipalActivity::class.java))
        finish()
    }
}

