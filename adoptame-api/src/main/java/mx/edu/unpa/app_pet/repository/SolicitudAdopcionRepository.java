package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Integer> {
    // Para ver las solicitudes que ha hecho un usuario específico
    List<SolicitudAdopcion> findByIdUsuarioSolicitante(Integer idUsuarioSolicitante);

    // Para ver todas las solicitudes que tiene una mascota en particular
    List<SolicitudAdopcion> findByIdMascota(Integer idMascota);
    // Consulta JPQL para obtener las solicitudes cuyas mascotas pertenecen al donador especificado
    @Query("SELECT s FROM SolicitudAdopcion s WHERE s.idMascota IN (SELECT m.idMascota FROM Mascota m WHERE m.idUsuarioDonador = :idDonador)")
    List<SolicitudAdopcion> findByDonadorId(@Param("idDonador") Integer idDonador);

    // Para el Endpoint 2: Buscar solicitudes de una mascota por estado
    List<SolicitudAdopcion> findByIdMascotaAndEstadoSolicitud(Integer idMascota, String estadoSolicitud);
    Optional<SolicitudAdopcion> findByIdMascotaAndIdUsuarioSolicitante(Integer idMascota, Integer idUsuarioSolicitante);
}
