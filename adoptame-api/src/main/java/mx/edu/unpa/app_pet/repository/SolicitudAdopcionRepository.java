package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {
    // Para ver las solicitudes que ha hecho un usuario específico
    List<SolicitudAdopcion> findByIdUsuarioSolicitante(Integer idUsuarioSolicitante);

    // Para ver todas las solicitudes que tiene una mascota en particular
    List<SolicitudAdopcion> findByIdMascota(Integer idMascota);
}
