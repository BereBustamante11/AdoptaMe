package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.MascotaDTO;
import mx.edu.unpa.app_pet.dtos.request.MascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.MascotaResponseDTO;

import java.util.List;

public interface MascotaService {
    // Retorna una lista de Responses
    List<MascotaResponseDTO> obtenerMascotasDisponibles();

    // Recibe un Request y retorna un Response
    MascotaResponseDTO registrarMascota(MascotaRequestDTO requestDTO);

    // Retorna un Response
    MascotaResponseDTO obtenerPorId(Integer id);
}
