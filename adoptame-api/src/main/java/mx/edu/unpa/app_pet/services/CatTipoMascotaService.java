package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.request.CatTipoMascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.CatTipoMascotaResponseDTO;

import java.util.List;

public interface CatTipoMascotaService {
    CatTipoMascotaResponseDTO crearTipoMascota(CatTipoMascotaRequestDTO requestDTO);
    List<CatTipoMascotaResponseDTO> obtenerTiposActivos();
}
