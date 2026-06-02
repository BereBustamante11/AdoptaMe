package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.Adopcion;
import mx.edu.unpa.app_pet.dtos.request.AdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.AdopcionResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdopcionMapper {
    Adopcion toEntity(AdopcionRequestDTO requestDTO);
    AdopcionResponseDTO toResponseDto(Adopcion adopcion);
    List<AdopcionResponseDTO> toResponseDtoList(List<Adopcion> adopciones);
}
