package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.SolicitudAdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudAdopcionResponseDTO;
import mx.edu.unpa.app_pet.dtos.response.SolicitudDonadorResponseDTO;
import mx.edu.unpa.app_pet.services.SolicitudAdopcionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudAdopcionController {

    private final SolicitudAdopcionService solicitudService;

    public SolicitudAdopcionController(SolicitudAdopcionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudAdopcionResponseDTO> crearSolicitud(@RequestBody SolicitudAdopcionRequestDTO requestDTO) {
        SolicitudAdopcionResponseDTO nuevaSolicitud = solicitudService.crearSolicitud(requestDTO);
        return new ResponseEntity<>(nuevaSolicitud, HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<SolicitudAdopcionResponseDTO>> obtenerMisSolicitudes(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(solicitudService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<SolicitudAdopcionResponseDTO>> obtenerSolicitudesPorMascota(@PathVariable Integer idMascota) {
        return ResponseEntity.ok(solicitudService.obtenerPorMascota(idMascota));
    }

    // Endpoint para cambiar el estado de la solicitud (ej. de PENDIENTE a APROBADA)
    @PatchMapping("/{idSolicitud}/estado")
    public ResponseEntity<SolicitudAdopcionResponseDTO> cambiarEstado(
            @PathVariable Integer idSolicitud,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estadoSolicitud");
        SolicitudAdopcionResponseDTO actualizada = solicitudService.actualizarEstado(idSolicitud, nuevoEstado);
        return ResponseEntity.ok(actualizada);
    }
    @GetMapping("/donador/{idUsuario}")
    public ResponseEntity<List<SolicitudDonadorResponseDTO>> obtenerSolicitudesDonador(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(solicitudService.obtenerSolicitudesParaDonador(idUsuario));
    }
}
