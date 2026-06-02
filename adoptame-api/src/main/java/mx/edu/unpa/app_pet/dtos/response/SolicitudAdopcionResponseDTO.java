package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudAdopcionResponseDTO {
    private Integer idSolicitud;
    private Integer idMascota;
    private Integer idUsuarioSolicitante;
    private String mensaje;
    private String estadoSolicitud;
    private LocalDateTime fechaSolicitud;
}
