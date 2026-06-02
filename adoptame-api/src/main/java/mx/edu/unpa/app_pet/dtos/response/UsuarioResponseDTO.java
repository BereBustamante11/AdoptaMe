package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Integer idUsuario; // ¡Aquí sí se incluye!
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
}
