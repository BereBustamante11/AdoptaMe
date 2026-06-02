package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import mx.edu.unpa.app_pet.dtos.request.SolicitudAdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudAdopcionResponseDTO;
import mx.edu.unpa.app_pet.mappers.SolicitudAdopcionMapper;
import mx.edu.unpa.app_pet.repository.SolicitudAdopcionRepository;
import mx.edu.unpa.app_pet.services.SolicitudAdopcionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final SolicitudAdopcionMapper solicitudMapper;

    public SolicitudAdopcionServiceImpl(SolicitudAdopcionRepository solicitudRepository, SolicitudAdopcionMapper solicitudMapper) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudMapper = solicitudMapper;
    }

    @Override
    public SolicitudAdopcionResponseDTO crearSolicitud(SolicitudAdopcionRequestDTO requestDTO) {
        SolicitudAdopcion solicitud = solicitudMapper.toEntity(requestDTO);

        // Toda solicitud nueva inicia como PENDIENTE
        solicitud.setEstadoSolicitud("PENDIENTE");

        SolicitudAdopcion guardada = solicitudRepository.save(solicitud);
        return solicitudMapper.toResponseDto(guardada);
    }

    @Override
    public List<SolicitudAdopcionResponseDTO> obtenerPorUsuario(Integer idUsuario) {
        List<SolicitudAdopcion> solicitudes = solicitudRepository.findByIdUsuarioSolicitante(idUsuario);
        return solicitudMapper.toResponseDtoList(solicitudes);
    }

    @Override
    public List<SolicitudAdopcionResponseDTO> obtenerPorMascota(Integer idMascota) {
        List<SolicitudAdopcion> solicitudes = solicitudRepository.findByIdMascota(idMascota);
        return solicitudMapper.toResponseDtoList(solicitudes);
    }

    @Override
    public SolicitudAdopcionResponseDTO actualizarEstado(Integer idSolicitud, String nuevoEstado) {
        SolicitudAdopcion solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // APROBADA, RECHAZADA, etc.
        solicitud.setEstadoSolicitud(nuevoEstado);
        SolicitudAdopcion actualizada = solicitudRepository.save(solicitud);

        return solicitudMapper.toResponseDto(actualizada);
    }
}
