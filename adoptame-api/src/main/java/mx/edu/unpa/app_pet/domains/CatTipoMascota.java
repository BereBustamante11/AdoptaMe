package mx.edu.unpa.app_pet.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CatTipoMascota")
@Data
@NoArgsConstructor
public class CatTipoMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoMascota")
    private Integer idTipoMascota;

    @Column(nullable = false, length = 50)
    private String descripcion;

    private Boolean activo = true;
}
