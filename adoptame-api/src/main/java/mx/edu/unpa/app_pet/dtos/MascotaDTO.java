package mx.edu.unpa.app_pet.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MascotaDTO {
    private Integer idMascota;
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
