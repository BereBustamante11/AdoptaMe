package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.CatTipoMascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatTipoMascotaRepository extends JpaRepository<CatTipoMascota, Integer> {
    // Solo queremos mandarle a la app móvil los tipos que estén activos
    List<CatTipoMascota> findByActivoTrue();
}
