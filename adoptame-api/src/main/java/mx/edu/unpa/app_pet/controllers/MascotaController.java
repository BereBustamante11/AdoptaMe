package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.MascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.MascotaResponseDTO;
import mx.edu.unpa.app_pet.services.MascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*") // Ajustar según el frontend
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MascotaResponseDTO>> getMascotasDisponibles() {
        return ResponseEntity.ok(mascotaService.obtenerMascotasDisponibles());
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDTO> createMascota(@RequestBody MascotaRequestDTO requestDTO) {
        MascotaResponseDTO nuevaMascota = mascotaService.registrarMascota(requestDTO);
        return new ResponseEntity<>(nuevaMascota, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> getMascotaById(@PathVariable Integer id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }
}