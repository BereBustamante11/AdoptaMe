package mx.edu.unpa.app_pet.domains;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SolicitudAdopcion")
@Data
@NoArgsConstructor
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSolicitud")
    private Integer idSolicitud;

    @Column(name = "idMascota", nullable = false)
    private Integer idMascota;

    @Column(name = "idUsuarioSolicitante", nullable = false)
    private Integer idUsuarioSolicitante;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "estadoSolicitud", nullable = false)
    private String estadoSolicitud = "PENDIENTE"; // PENDIENTE, APROBADA, RECHAZADA

    @Column(name = "fechaSolicitud", insertable = false, updatable = false)
    private LocalDateTime fechaSolicitud;
}
