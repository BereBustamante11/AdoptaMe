package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String password;
    private String telefono;
}