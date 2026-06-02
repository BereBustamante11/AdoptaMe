package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class SolicitudAdopcionRequestDTO {
    private Integer idMascota;
    private Integer idUsuarioSolicitante;
    private String mensaje;
}
