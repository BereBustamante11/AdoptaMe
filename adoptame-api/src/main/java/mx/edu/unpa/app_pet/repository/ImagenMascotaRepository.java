package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.ImagenMascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenMascotaRepository extends JpaRepository<ImagenMascota, Integer> {
    // Muy útil para que la app móvil recupere todas las fotos de una mascota en específico
    List<ImagenMascota> findByIdMascota(Integer idMascota);
}
