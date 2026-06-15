package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {
    private Integer idUsuario; // ¡Aquí sí se incluye!
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
    // Agregados para que coincidan con la data class de Kotlin
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private String urlFotoPerfil;
}
