package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class AdopcionRequestDTO {
    private Integer idSolicitud;
    private Integer idMascota;
    private Integer idUsuarioAdoptante;
    private String observaciones;
}
