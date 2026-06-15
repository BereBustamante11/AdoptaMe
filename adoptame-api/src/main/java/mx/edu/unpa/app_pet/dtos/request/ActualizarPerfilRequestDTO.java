package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class ActualizarPerfilRequestDTO {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String telefono;
    private String urlFotoPerfil;
}