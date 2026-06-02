package mx.edu.unpa.app_pet.mappers;

import mx.edu.unpa.app_pet.domains.Usuario;
import mx.edu.unpa.app_pet.dtos.request.UsuarioRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.UsuarioResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // Transforma los datos de registro a la entidad de Base de Datos
    Usuario toEntity(UsuarioRequestDTO requestDTO);

    // Transforma la entidad de la Base de Datos a la respuesta limpia para el móvil
    UsuarioResponseDTO toResponseDto(Usuario usuario);
}
