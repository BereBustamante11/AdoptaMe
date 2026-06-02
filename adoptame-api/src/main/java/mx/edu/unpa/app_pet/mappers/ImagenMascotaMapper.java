package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.ImagenMascota;
import mx.edu.unpa.app_pet.dtos.response.ImagenMascotaResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImagenMascotaMapper {
    ImagenMascotaResponseDTO toResponseDto(ImagenMascota imagenMascota);
    List<ImagenMascotaResponseDTO> toResponseDtoList(List<ImagenMascota> imagenes);
}
