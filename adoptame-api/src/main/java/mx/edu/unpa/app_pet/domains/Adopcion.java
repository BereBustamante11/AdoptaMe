package mx.edu.unpa.app_pet.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Adopcion")
@Data
@NoArgsConstructor
public class Adopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdopcion")
    private Integer idAdopcion;

    @Column(name = "idSolicitud", nullable = false)
    private Integer idSolicitud;

    @Column(name = "idMascota", nullable = false)
    private Integer idMascota;

    @Column(name = "idUsuarioAdoptante", nullable = false)
    private Integer idUsuarioAdoptante;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fechaAdopcion", insertable = false, updatable = false)
    private LocalDateTime fechaAdopcion;
}
