package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {
    List<Mascota> findByActivoTrueAndEstadoAdopcion(String estado);
}