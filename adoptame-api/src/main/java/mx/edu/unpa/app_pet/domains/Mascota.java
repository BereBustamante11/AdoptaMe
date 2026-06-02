package mx.edu.unpa.app_pet.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Mascota")
@Data
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMascota")
    private Integer idMascota;

    @Column(name = "idUsuarioDonador", nullable = false)
    private Integer idUsuarioDonador;

    @Column(name = "idTipoMascota", nullable = false)
    private Integer idTipoMascota;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String raza;

    @Column(nullable = false)
    private String sexo; // MACHO, HEMBRA

    @Column(name = "edadAproximada", length = 50)
    private String edadAproximada;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estadoAdopcion", nullable = false)
    private String estadoAdopcion = "DISPONIBLE";

    private Boolean activo = true;

    @Column(name = "fechaPublicacion", insertable = false, updatable = false)
    private LocalDateTime fechaPublicacion;
}
