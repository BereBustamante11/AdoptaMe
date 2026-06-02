package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.CatTipoMascota;
import mx.edu.unpa.app_pet.dtos.request.CatTipoMascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.CatTipoMascotaResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CatTipoMascotaMapper {
    CatTipoMascota toEntity(CatTipoMascotaRequestDTO requestDTO);
    CatTipoMascotaResponseDTO toResponseDto(CatTipoMascota tipoMascota);
    List<CatTipoMascotaResponseDTO> toResponseDtoList(List<CatTipoMascota> tiposMascota);
}
