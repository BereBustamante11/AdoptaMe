package mx.edu.unpa.app_pet.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImagenMascotaResponseDTO {
    private Integer idImagen;
    private Integer idMascota;
    private String urlImagen;
    private Boolean imagenPrincipal;
    private LocalDateTime fechaSubida;
}
