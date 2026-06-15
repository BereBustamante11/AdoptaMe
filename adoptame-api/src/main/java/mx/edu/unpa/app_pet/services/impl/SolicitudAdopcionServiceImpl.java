package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.SolicitudAdopcion;
import mx.edu.unpa.app_pet.dtos.request.SolicitudAdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudAdopcionResponseDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudDonadorResponseDTO;
import mx.edu.unpa.app_pet.mappers.SolicitudAdopcionMapper;
import mx.edu.unpa.app_pet.repository.MascotaRepository;
import mx.edu.unpa.app_pet.repository.SolicitudAdopcionRepository;
import mx.edu.unpa.app_pet.repository.UsuarioRepository;
import mx.edu.unpa.app_pet.services.SolicitudAdopcionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudAdopcionServiceImpl implements SolicitudAdopcionService {

    private final SolicitudAdopcionRepository solicitudRepository;
    private final SolicitudAdopcionMapper solicitudMapper;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudAdopcionServiceImpl(SolicitudAdopcionRepository solicitudRepository, SolicitudAdopcionMapper solicitudMapper, MascotaRepository mascotaRepository, UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.solicitudMapper = solicitudMapper;
        this.mascotaRepository = mascotaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public SolicitudAdopcionResponseDTO crearSolicitud(SolicitudAdopcionRequestDTO requestDTO) {
        Optional<SolicitudAdopcion> solicitudExistente = solicitudRepository
                .findByIdMascotaAndIdUsuarioSolicitante(
                        requestDTO.getIdMascota(),
                        requestDTO.getIdUsuarioSolicitante()
                );

        if (solicitudExistente.isPresent()) {
            // 2. Si ya existe, ACTUALIZAMOS la pasada en lugar de crear una nueva
            SolicitudAdopcion solicitud = solicitudExistente.get();

            // Reemplazamos el mensaje viejo por el nuevo
            solicitud.setMensaje(requestDTO.getMensaje());

            // Forzamos el estado a PENDIENTE (muy útil por si su solicitud pasada
            // había sido "RECHAZADA" y el usuario está volviendo a intentar)
            solicitud.setEstadoSolicitud("PENDIENTE");

            SolicitudAdopcion actualizada = solicitudRepository.save(solicitud);
            return solicitudMapper.toResponseDto(actualizada);
        }

        // 3. Si NO existe, creamos una totalmente nueva (Lógica original)
        SolicitudAdopcion nuevaSolicitud = solicitudMapper.toEntity(requestDTO);
        nuevaSolicitud.setEstadoSolicitud("PENDIENTE");

        SolicitudAdopcion guardada = solicitudRepository.save(nuevaSolicitud);
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

    @Override
    public List<SolicitudDonadorResponseDTO> obtenerSolicitudesParaDonador(Integer idDonador) {
        List<SolicitudAdopcion> solicitudes = solicitudRepository.findByDonadorId(idDonador);

        return solicitudes.stream().map(solicitud -> {
            SolicitudDonadorResponseDTO dto = new SolicitudDonadorResponseDTO();
            dto.setIdSolicitud(solicitud.getIdSolicitud());
            dto.setIdMascota(solicitud.getIdMascota());
            dto.setIdUsuarioSolicitante(solicitud.getIdUsuarioSolicitante());
            dto.setMensaje(solicitud.getMensaje());
            dto.setEstadoSolicitud(solicitud.getEstadoSolicitud());
            dto.setFechaSolicitud(solicitud.getFechaSolicitud());

            // JOIN lógico a Mascota
            mascotaRepository.findById(solicitud.getIdMascota()).ifPresent(mascota ->
                    dto.setNombreMascota(mascota.getNombre())
            );

            // JOIN lógico a Usuario (Solicitante) con concatenación
            usuarioRepository.findById(solicitud.getIdUsuarioSolicitante()).ifPresent(usuario ->
                    dto.setNombreSolicitante(usuario.getNombre() + " " + usuario.getApellidoPaterno())
            );

            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}
