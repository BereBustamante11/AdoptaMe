package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.Mascota;
import mx.edu.unpa.app_pet.dtos.request.MascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.MascotaResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MascotaMapper {

    // Transforma el Request que viene del móvil a la entidad de la BD
    Mascota toEntity(MascotaRequestDTO requestDTO);

    // Transforma la entidad de la BD al Response que espera el móvil
    MascotaResponseDTO toResponseDto(Mascota mascota);

    // Transforma listas de entidades a listas de respuestas
    List<MascotaResponseDTO> toResponseDtoList(List<Mascota> mascotas);
}
