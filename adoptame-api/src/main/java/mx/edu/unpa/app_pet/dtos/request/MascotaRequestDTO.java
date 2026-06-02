package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class MascotaRequestDTO {
    private Integer idUsuarioDonador;
    private Integer idTipoMascota;
    private String nombre;
    private String raza;
    private String sexo;
    private String edadAproximada;
    private String descripcion;
}
