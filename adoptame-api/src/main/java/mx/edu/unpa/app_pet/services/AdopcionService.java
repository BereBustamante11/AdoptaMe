package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.request.AdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.AdopcionResponseDTO;

import java.util.List;

public interface AdopcionService {
    AdopcionResponseDTO concretarAdopcion(AdopcionRequestDTO requestDTO);
    List<AdopcionResponseDTO> obtenerAdopcionesPorUsuario(Integer idUsuario);
}
