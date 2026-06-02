package mx.edu.unpa.app_pet.dtos.request;

import lombok.Data;

@Data
public class CatTipoMascotaRequestDTO {
    // Para registrar un nuevo tipo solo necesitamos saber cómo se llama
    private String descripcion;
}
