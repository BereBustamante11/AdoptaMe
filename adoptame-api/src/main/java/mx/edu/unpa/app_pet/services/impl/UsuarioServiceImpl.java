package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.Usuario;
import mx.edu.unpa.app_pet.dtos.request.LoginRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.UsuarioRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.UsuarioResponseDTO;
import mx.edu.unpa.app_pet.mappers.UsuarioMapper;
import mx.edu.unpa.app_pet.repository.UsuarioRepository;
import mx.edu.unpa.app_pet.services.UsuarioService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    // Inyección limpia por constructor
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO requestDTO) {
        // Validación básica: que no repita correo
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        // 1. Convertimos el Request DTO a la Entidad con MapStruct
        Usuario usuario = usuarioMapper.toEntity(requestDTO);

        // Forzamos valores por defecto para nuevos usuarios
        usuario.setActivo(true);

        // 2. Guardamos en la base de datos (Contraseña en texto plano para fines escolares)
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 3. Devolvemos el Response DTO sin la contraseña
        return usuarioMapper.toResponseDto(usuarioGuardado);
    }

    @Override
    public UsuarioResponseDTO login(LoginRequestDTO loginDTO) {
        // Busca directamente coincidencia exacta de correo, contraseña y que esté activo
        Usuario usuario = usuarioRepository.findByEmailAndPasswordAndActivoTrue(
                loginDTO.getEmail(),
                loginDTO.getPassword()
        ).orElseThrow(() -> new RuntimeException("Correo o contraseña incorrectos."));

        // Retorna la información del usuario logueado mapeado a su Response DTO
        return usuarioMapper.toResponseDto(usuario);
    }
}