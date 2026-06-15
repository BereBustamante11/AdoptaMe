package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.ActualizarPerfilRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.LoginRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.UsuarioRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.FotoPerfilResponseDTO;
import mx.edu.unpa.app_pet.dtos.response.UsuarioResponseDTO;
import mx.edu.unpa.app_pet.services.FileStorageService;
import mx.edu.unpa.app_pet.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FileStorageService fileStorageService;

    public UsuarioController(UsuarioService usuarioService, FileStorageService fileStorageService) {
        this.usuarioService = usuarioService;
        this.fileStorageService = fileStorageService;
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
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }
    // NUEVO ENDPOINT: Actualizar perfil
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(
            @PathVariable Integer id,
            @RequestBody ActualizarPerfilRequestDTO request) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, request));
    }

    // NUEVO ENDPOINT: Subir foto de perfil (Multipart)
    @PostMapping("/{id}/foto")
    public ResponseEntity<FotoPerfilResponseDTO> subirFotoPerfil(
            @PathVariable Integer id,
            @RequestParam("archivo") MultipartFile archivo) {

        // 1. Guardamos la imagen en el servidor usando tu servicio existente
        String urlFoto = fileStorageService.guardarImagen(archivo);

        // 2. Actualizamos el registro del usuario en la BD con la nueva URL
        usuarioService.actualizarUrlFoto(id, urlFoto);

        // 3. Devolvemos el DTO con el formato JSON que espera el frontend
        return ResponseEntity.ok(new FotoPerfilResponseDTO(urlFoto));
    }
}
