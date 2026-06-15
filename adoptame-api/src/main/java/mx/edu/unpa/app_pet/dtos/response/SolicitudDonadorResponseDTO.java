package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SolicitudDonadorResponseDTO {
    private Integer idSolicitud;
    private Integer idMascota;
    private String nombreMascota;
    private Integer idUsuarioSolicitante;
    private String nombreSolicitante; // nombre + apellidoPaterno
    private String mensaje;
    private String estadoSolicitud;
    private LocalDateTime fechaSolicitud;
}
