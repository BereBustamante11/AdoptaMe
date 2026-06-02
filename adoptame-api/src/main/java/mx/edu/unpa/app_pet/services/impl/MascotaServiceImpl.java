package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.Mascota;
import mx.edu.unpa.app_pet.dtos.request.MascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.MascotaResponseDTO;
import mx.edu.unpa.app_pet.mappers.MascotaMapper;
import mx.edu.unpa.app_pet.repository.MascotaRepository;
import mx.edu.unpa.app_pet.services.MascotaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final MascotaMapper mascotaMapper;

    public MascotaServiceImpl(MascotaRepository mascotaRepository, MascotaMapper mascotaMapper) {
        this.mascotaRepository = mascotaRepository;
        this.mascotaMapper = mascotaMapper;
    }

    @Override
    public List<MascotaResponseDTO> obtenerMascotasDisponibles() {
        List<Mascota> disponibles = mascotaRepository.findByActivoTrueAndEstadoAdopcion("DISPONIBLE");
        // Convertimos la lista de entidades a lista de Response DTOs
        return mascotaMapper.toResponseDtoList(disponibles);
    }

    @Override
    public MascotaResponseDTO registrarMascota(MascotaRequestDTO requestDTO) {
        // 1. Convertimos el Request a Entidad
        Mascota mascota = mascotaMapper.toEntity(requestDTO);

        // Opcional: Como es un registro nuevo, forzamos que inicie en DISPONIBLE
        mascota.setEstadoAdopcion("DISPONIBLE");
        mascota.setActivo(true);

        // 2. Guardamos en la base de datos
        Mascota mascotaGuardada = mascotaRepository.save(mascota);

        // 3. Convertimos el resultado a un ResponseDTO limpio
        return mascotaMapper.toResponseDto(mascotaGuardada);
    }

    @Override
    public MascotaResponseDTO obtenerPorId(Integer id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        return mascotaMapper.toResponseDto(mascota);
    }
}