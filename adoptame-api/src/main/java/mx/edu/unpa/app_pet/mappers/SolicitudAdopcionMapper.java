package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import mx.edu.unpa.app_pet.dtos.request.SolicitudAdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudAdopcionResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudAdopcionMapper {
    SolicitudAdopcion toEntity(SolicitudAdopcionRequestDTO requestDTO);
    SolicitudAdopcionResponseDTO toResponseDto(SolicitudAdopcion solicitud);
    List<SolicitudAdopcionResponseDTO> toResponseDtoList(List<SolicitudAdopcion> solicitudes);
}
