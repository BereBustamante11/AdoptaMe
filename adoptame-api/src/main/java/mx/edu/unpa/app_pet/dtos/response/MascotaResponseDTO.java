package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MascotaResponseDTO {
    private Integer idMascota; // ¡Indispensable para que la app sepa qué ID tiene!
    private Integer idUsuarioDonador;
    private Integer idTipoMascota;
    private String nombre;
    private String raza;
    private String sexo;
    private String edadAproximada;
    private String descripcion;
    private String estadoAdopcion;
    private LocalDateTime fechaPublicacion;
}
