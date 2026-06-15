package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.Adopcion;
import mx.edu.unpa.app_pet.domains.Mascota;
import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import mx.edu.unpa.app_pet.dtos.request.AdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.AdopcionResponseDTO;
import mx.edu.unpa.app_pet.mappers.AdopcionMapper;
import mx.edu.unpa.app_pet.repository.AdopcionRepository;
import mx.edu.unpa.app_pet.repository.MascotaRepository;
import mx.edu.unpa.app_pet.repository.SolicitudAdopcionRepository;
import mx.edu.unpa.app_pet.services.AdopcionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdopcionServiceImpl implements AdopcionService {

    private final AdopcionRepository adopcionRepository;
    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionRepository solicitudRepository;
    private final AdopcionMapper adopcionMapper;

    public AdopcionServiceImpl(AdopcionRepository adopcionRepository,
                               MascotaRepository mascotaRepository,
                               SolicitudAdopcionRepository solicitudRepository,
                               AdopcionMapper adopcionMapper) {
        this.adopcionRepository = adopcionRepository;
        this.mascotaRepository = mascotaRepository;
        this.solicitudRepository = solicitudRepository;
        this.adopcionMapper = adopcionMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)// Esto asegura que si algo falla, no se guarde a medias.
    public AdopcionResponseDTO concretarAdopcion(AdopcionRequestDTO requestDTO) {
        // 1. Guardar el registro de Adopción
        Adopcion adopcion = adopcionMapper.toEntity(requestDTO);
        Adopcion adopcionGuardada = adopcionRepository.save(adopcion);

        // 2. Actualizar la Mascota a 'ADOPTADO'
        Mascota mascota = mascotaRepository.findById(requestDTO.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        mascota.setEstadoAdopcion("ADOPTADO");
        mascotaRepository.save(mascota);

        // 3. Actualizar la Solicitud a 'APROBADA'
        SolicitudAdopcion solicitud = solicitudRepository.findById(requestDTO.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitud.setEstadoSolicitud("APROBADA");
        solicitudRepository.save(solicitud);
        List<SolicitudAdopcion> solicitudesPendientes = solicitudRepository
                .findByIdMascotaAndEstadoSolicitud(requestDTO.getIdMascota(), "PENDIENTE");

        for (SolicitudAdopcion otraSolicitud : solicitudesPendientes) {
            // Evitamos alterar la solicitud que acabamos de aprobar por seguridad
            if (!otraSolicitud.getIdSolicitud().equals(requestDTO.getIdSolicitud())) {
                otraSolicitud.setEstadoSolicitud("RECHAZADA");
                solicitudRepository.save(otraSolicitud);
            }
        }

        // 4. Retornar la respuesta al cliente
        return adopcionMapper.toResponseDto(adopcionGuardada);
    }

    @Override
    public List<AdopcionResponseDTO> obtenerAdopcionesPorUsuario(Integer idUsuario) {
        List<Adopcion> misAdopciones = adopcionRepository.findByIdUsuarioAdoptante(idUsuario);
        return adopcionMapper.toResponseDtoList(misAdopciones);
    }
}
