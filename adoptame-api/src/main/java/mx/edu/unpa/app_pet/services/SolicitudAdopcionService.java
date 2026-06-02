package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.request.SolicitudAdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudAdopcionResponseDTO;

import java.util.List;

public interface SolicitudAdopcionService {
    SolicitudAdopcionResponseDTO crearSolicitud(SolicitudAdopcionRequestDTO requestDTO);
    List<SolicitudAdopcionResponseDTO> obtenerPorUsuario(Integer idUsuario);
    List<SolicitudAdopcionResponseDTO> obtenerPorMascota(Integer idMascota);
    SolicitudAdopcionResponseDTO actualizarEstado(Integer idSolicitud, String nuevoEstado);
}
