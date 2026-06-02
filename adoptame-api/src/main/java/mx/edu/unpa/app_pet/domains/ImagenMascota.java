package mx.edu.unpa.app_pet.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ImagenMascota")
@Data
@NoArgsConstructor
public class ImagenMascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idImagen")
    private Integer idImagen;

    @Column(name = "idMascota", nullable = false)
    private Integer idMascota;

    @Column(name = "urlImagen", nullable = false, length = 255)
    private String urlImagen;

    @Column(name = "imagenPrincipal")
    private Boolean imagenPrincipal = false;

    @Column(name = "fechaSubida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;
}
