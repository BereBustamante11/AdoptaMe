package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.LoginRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.UsuarioRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.UsuarioResponseDTO;
import mx.edu.unpa.app_pet.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody UsuarioRequestDTO request) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // Si las credenciales son correctas, retorna el UsuarioDTO (sin password).
        // La app móvil puede guardar el idUsuario en SharedPreferences (Android) o similar
        // para recordar quién inició sesión.
        return ResponseEntity.ok(usuarioService.login(loginRequestDTO));
    }
}
