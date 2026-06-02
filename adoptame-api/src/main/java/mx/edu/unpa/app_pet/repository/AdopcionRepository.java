package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.Adopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdopcionRepository extends JpaRepository<Adopcion, Integer> {
    // Te servirá para mostrar en la app "Mis mascotas adoptadas"
    List<Adopcion> findByIdUsuarioAdoptante(Integer idUsuarioAdoptante);
}
