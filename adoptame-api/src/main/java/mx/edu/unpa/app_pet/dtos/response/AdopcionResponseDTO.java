package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdopcionResponseDTO {
    private Integer idAdopcion;
    private Integer idSolicitud;
    private Integer idMascota;
    private Integer idUsuarioAdoptante;
    private String observaciones;
    private LocalDateTime fechaAdopcion;
}
