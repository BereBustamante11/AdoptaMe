package mx.edu.unpa.app_pet.services;

import mx.edu.unpa.app_pet.dtos.response.ImagenMascotaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagenMascotaService {
    ImagenMascotaResponseDTO subirImagenMascota(Integer idMascota, Boolean esPrincipal, MultipartFile archivo);
    List<ImagenMascotaResponseDTO> obtenerImagenesPorMascota(Integer idMascota);
}
