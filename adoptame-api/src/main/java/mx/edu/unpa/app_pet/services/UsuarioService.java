package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.request.ActualizarPerfilRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.LoginRequestDTO;
import mx.edu.unpa.app_pet.dtos.request.UsuarioRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO requestDTO);
    UsuarioResponseDTO login(LoginRequestDTO loginDTO);
    UsuarioResponseDTO getUsuarioById(Integer id);
    UsuarioResponseDTO actualizarPerfil(Integer id, ActualizarPerfilRequestDTO request);
    void actualizarUrlFoto(Integer id, String urlFoto);
}
