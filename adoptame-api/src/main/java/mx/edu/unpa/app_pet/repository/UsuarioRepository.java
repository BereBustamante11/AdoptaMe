package mx.edu.unpa.app_pet.repository;

import mx.edu.unpa.app_pet.domains.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmailAndPasswordAndActivoTrue(String email, String password);
    boolean existsByEmail(String email);
}
